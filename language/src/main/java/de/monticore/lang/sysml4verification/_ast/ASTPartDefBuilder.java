package de.monticore.lang.sysml4verification._ast;

import de.monticore.ast.Comment;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteralBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ASTPartDefBuilder extends ASTPartDefBuilderTOP {

  private final Optional<ASTPartDef> originalReference;

  public ASTPartDefBuilder() {
    super();
    this.originalReference = Optional.empty();
    this.setModifier(SysML4VerificationMill.modifierBuilder().build());
  }

  public ASTPartDefBuilder(ASTPartDef referencePartDef) {
    super();
    this.originalReference = Optional.of(referencePartDef);
    this.setName(referencePartDef.getName());
    this.setModifier(referencePartDef.getModifier().deepClone());

    for (var element : referencePartDef.getSysMLElementList()) {
      this.addSysMLElement(element.deepClone());
    }

    for (var type : referencePartDef.getSpecializationList()) {
      this.addSpecialization(type.deepClone());
    }

    this.set_PreCommentList(referencePartDef.get_PreCommentList());
    this.set_PostCommentList(referencePartDef.get_PostCommentList());
  }

  /**
   * Transformiert die vorhandenen PartUsages zu ihrem, sofern vorhanden,
   * passenden HLR/LLR-Equivalent. "Passende" Part Usages werden basierend auf
   * {@link PartDefSymbol#getRefinementScore(PartDefSymbol)} ausgewählt.
   * @param targetType
   */
  public ASTPartDefBuilder transformPartUsages(ASTSysMLReqType targetType) {
    if (targetType != ASTSysMLReqType.HLR && targetType != ASTSysMLReqType.LLR) {
      throw new UnsupportedOperationException("Can only convert part usages to HLR or LLR");
    }

    for (var p : List.copyOf(this.getSysMLElementList())) {
      if (p instanceof ASTPartUsage) {
        try {
          var originalPartDefOpt = SysMLv2Mill.globalScope().resolvePartDef(((ASTPartUsage) p).getSpecializationList().get(0).getSuperTypes(0).printType());
          if (originalPartDefOpt.isEmpty()) {
            // When we can't find the original symbol. We can also not check for corresponding HLRs.
            continue;
          }
          var originalPartDef = originalPartDefOpt.get();
          if (originalPartDef.getRequirementType() == targetType) {
            // Nothing to do here.
            continue;
          }

          var candidates = targetType == ASTSysMLReqType.HLR
              ? originalPartDef.getTransitiveRefinements()
              : originalPartDef.getTransitiveRefiners();

          if (candidates.isEmpty()) {
            continue;
          }

          candidates = candidates.stream()
              .sorted(Comparator.comparingInt(candidate -> candidate.getRefinementScore(originalPartDef)))
              .collect(Collectors.toList());

          var chosenCandidate = candidates.get(candidates.size() - 1);
          // TODO joiner Joiners.DOT.toString()
          var hlrEquivalentQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(
              List.of(chosenCandidate.getFullName().split("\\."))).build();
          var hlrEquivalentQMcType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(hlrEquivalentQName).build();

          ((ASTPartUsage) p).getSpecializationList().get(0).getSuperTypesList().set(0, hlrEquivalentQMcType);
        } catch (Exception e) {
          // continue
        }
      }
    }

    return this;
  }

  /**
   * Removes all SysMLElements that are considered to be only part of a given {@link ASTSysMLReqType}.
   * @param propertyType The type of SysML elements to remove.
   */
  public ASTPartDefBuilder removeProperties(ASTSysMLReqType propertyType) {
    if (propertyType == ASTSysMLReqType.LLR || propertyType == ASTSysMLReqType.MIXED) {
      this.removeIfSysMLElement(e -> e instanceof ASTStateUsage);
    }

    if (propertyType == ASTSysMLReqType.HLR || propertyType == ASTSysMLReqType.MIXED) {
      this.removeIfSysMLElement(e -> e instanceof ASTRequirementUsage);
      this.removeIfSysMLElement(e -> e instanceof ASTConstraintUsage);
    }

    return realBuilder;
  }

  /**
   * Hilfsfunktion um zu Testen, ob die Referenz-PartDef des Builders eine bestimmte Eigenschaft erfüllt.
   * @param predicate Die Eigenschaft die getestet werden soll.
   * @param func Die Funktion die ausgeführt werden soll, wenn die Eigenschaft erfüllt ist.
   */
  public ASTPartDefBuilder ifReference(Predicate<de.monticore.lang.sysmlv2._ast.ASTPartDef> predicate, Function<ASTPartDefBuilder, ASTPartDefBuilder> func) {
    if (originalReference.isEmpty()) {
      throw new IllegalStateException("Cannot apply ifReference on a builder that has no reference");
    }
    if (predicate.test(originalReference.get())) {
      return func.apply(this);
    } else {
      return this;
    }
  }

  /**
   * Simplifies adding a new {@link de.monticore.lang.sysmlv2._ast.ASTPortUsage} to {@link de.monticore.lang.sysmlv2._ast.ASTPartDef#sysMLElements}.
   * @param name The name of the port usage.
   * @param type The type of the port usage.
   */
  public ASTPartDefBuilder addPortUsage(String name, ASTMCType type) {
    var portUsage = SysMLv2Mill.portUsageBuilder()
        .setName(name)
        .addSpecialization(SysMLv2Mill.sysMLTypingBuilder().addSuperTypes(type).build())
        .setModifier(SysMLv2Mill.modifierBuilder().build())
        .setDefaultValue(SysMLv2Mill.defaultValueBuilder().build())
        .build();

    this.addSysMLElement(portUsage);
    return this;
  }

  /**
   * Simplifies creating and adding a new empty constraint usage to {@link de.monticore.lang.sysmlv2._ast.ASTPartDef#sysMLElements}.
   * @param name The name of the constraint usage.
   */
  public ASTPartDefBuilder addEmptyConstraint(String name) {
    var fillOutComment = new Comment();
    fillOutComment.setText("/* TODO: Fill out */");

    var expr = new ASTLiteralExpressionBuilder().setLiteral(new ASTBooleanLiteralBuilder()
            .setSource(3) // 3 represents true
            .add_PreComment(fillOutComment)
            .build())
        .build();

    var constraint = new ASTConstraintUsageBuilder()
        .setModifier(new ASTModifierBuilder().build())
        .setName(name)
        .setRequire(true)
        .setExpression(expr)
        .build();

    var newRequirement = new ASTRequirementUsageBuilder()
        .setModifier(new ASTModifierBuilder().build())
        .setName(name)
        .addSysMLElement(constraint)
        .setSatisfy(true).build();

    this.getSysMLElementList().add(newRequirement);
    return this;
  }

  /**
   * Simplifies creating and adding a new empty state usage to {@link de.monticore.lang.sysmlv2._ast.ASTPartDef#sysMLElements}.
   * @param name The name of the state usage.
   */
  public ASTPartDefBuilder addEmptyStateUsage(String name) {
    var newStateUsage = SysMLv2Mill.stateUsageBuilder()
        .setName(name)
        .setDefaultValue(SysMLv2Mill.defaultValueBuilder().build())
        .setEntryAction(0, SysMLv2Mill.entryActionBuilder()
            .setActionUsage(SysMLv2Mill.actionUsageBuilder()
                .setName("Entry")
                .setModifier(SysMLv2Mill.modifierBuilder().build())
                .setDefaultValue(SysMLv2Mill.defaultValueBuilder().build())
                .build())
            .build())
        .addSysMLElement(
            SysMLv2Mill.sysMLSuccessionBuilder()
                .setSuccessionThen(
                    SysMLv2Mill.successionThenBuilder().setMCQualifiedName(
                        SysMLv2Mill.mCQualifiedNameBuilder().setParts(0, "Init").build()
                    ).build()
                )
                .setModifier(SysMLv2Mill.modifierBuilder().build())
                .build())
        .addSysMLElement(SysMLv2Mill.stateUsageBuilder()
            .setName("Init")
            .setModifier(SysMLv2Mill.modifierBuilder().build())
            .setDefaultValue(SysMLv2Mill.defaultValueBuilder().build())
            .build())
        .setModifier(SysMLv2Mill.modifierBuilder().build())
        .setExhibited(true)
        .build();

    this.getSysMLElementList().add(newStateUsage);
    return this;
  }

  public ASTPartDefBuilder addDecomposition(de.monticore.lang.sysmlv2._ast.ASTPartDef comp1, de.monticore.lang.sysmlv2._ast.ASTPartDef comp2) {
    if (originalReference.isEmpty()){
      throw new IllegalStateException("Adding a decomposition is only supported if a reference was given before.");
    }

    if (this.build().getSysMLElements(de.monticore.lang.sysmlv2._ast.ASTPortUsage.class).isEmpty()){
      return this;
    }

    var comp1Name = comp1.getName()
        .replaceAll("(?i)hlr","")
        .replaceAll("(?i)llr","")
        .toLowerCase();

    var comp2Name = comp2.getName()
        .replaceAll("(?i)hlr","")
        .replaceAll("(?i)llr","")
        .toLowerCase();

    if (comp2Name.equals(comp1Name)){
      comp2Name = comp2Name + "2";
    }

    this.addPartUsage(comp1Name, comp1);
    this.addPartUsage(comp2Name, comp2);

    // We don't calculate connection usages for now but later this is the place to add a calculated mapping.

    var referenceInputs = originalReference.get().getSysMLElements(de.monticore.lang.sysmlv2._ast.ASTPortUsage.class).stream()
        .filter(p -> p.estimatePortDirection() == de.monticore.lang.sysmlv2._ast.ASTPortUsage.Direction.IN)
        .collect(Collectors.toList());

    for (de.monticore.lang.sysmlv2._ast.ASTPortUsage input : referenceInputs){
      var connectionUsage = SysMLv2Mill.connectionUsageBuilder()
          .setSrc(
              SysMLv2Mill.endpointBuilder()
                  .setMCQualifiedName(
                      SysMLv2Mill.mCQualifiedNameBuilder()
                          .addParts(input.getName())
                          .build())
                  .build()
              )
          .setTgt(
              SysMLv2Mill.endpointBuilder()
                  .setMCQualifiedName(
                      SysMLv2Mill.mCQualifiedNameBuilder()
                          .addParts("/* TODO: add target */")
                          .build())
                  .build()
          )
          .build();

      this.getSysMLElementList().add(connectionUsage);
    }


    var referenceOutputs = originalReference.get().getSysMLElements(de.monticore.lang.sysmlv2._ast.ASTPortUsage.class).stream()
        .filter(p -> p.estimatePortDirection() == de.monticore.lang.sysmlv2._ast.ASTPortUsage.Direction.OUT)
        .collect(Collectors.toList());

    for (ASTPortUsage output : referenceOutputs){
      var connectionUsage = SysMLv2Mill.connectionUsageBuilder()
          .setSrc(
              SysMLv2Mill.endpointBuilder()
                  .setMCQualifiedName(
                      SysMLv2Mill.mCQualifiedNameBuilder()
                          .addParts("/* TODO: add source */")
                          .build()
                  )
                  .build())
          .setTgt(
              SysMLv2Mill.endpointBuilder()
                  .setMCQualifiedName(
                      SysMLv2Mill.mCQualifiedNameBuilder()
                          .addParts(output.getName())
                          .build()
                  )
                  .build())
          .build();

      this.getSysMLElementList().add(connectionUsage);
    }

    return this;
  }

  /**
   * Simplifies creating and adding a {@link ASTPartUsage} to {@link de.monticore.lang.sysmlv2._ast.ASTPartDef#sysMLElements}.
   * @param usageName The name of the part usage.
   * @param partDef The part definition that is used.
   */
  public ASTPartDefBuilder addPartUsage(String usageName, de.monticore.lang.sysmlv2._ast.ASTPartDef partDef){
    var partDefQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(List.of(partDef.getName().split("\\."))).build();
    var usageType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(partDefQName).build();
    this.addSysMLElement(SysMLv2Mill.partUsageBuilder()
        .setModifier(SysMLv2Mill.modifierBuilder().build())
        .setDefaultValue(new ASTDefaultValueBuilder().build())
        .setName(usageName)
        .addSpecialization(SysMLv2Mill.sysMLTypingBuilder().addSuperTypes(usageType).build())
        .build());

    return this;
  }

  /**
   * Simplifes creating and adding a new refinement to {@link de.monticore.lang.sysmlv2._ast.ASTPartDef#specializations}.
   * @param partDef The part definition that is refined.
   */
  public ASTPartDefBuilder addRefinement(de.monticore.lang.sysmlv2._ast.ASTPartDef partDef) {
    return this.addRefinement(partDef.getName());
  }

  /**
   * Simplifes creating and adding a new refinement to {@link ASTPartDef#specializations}.
   * @param partDef The part definition that is refined.
   */
  public ASTPartDefBuilder addRefinement(String partDef) {
    var partDefQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(List.of(partDef.split("\\."))).build();
    var refinementType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(partDefQName).build();
    var refinement = SysMLv2Mill.sysMLRefinementBuilder().addSuperTypes(refinementType).build();
    this.addSpecialization(refinement);
    return this;
  }

}
