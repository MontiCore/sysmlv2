package de.monticore.lang.sysmlv2._lsp.features.code_action.utils;

import de.monticore.ast.Comment;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsageBuilder;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsageBuilder;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateUsageBuilder;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteralBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.DecompositionUtils.estimatePortDirection;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.DecompositionUtils.getDecompositionMapping;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.RefinementAnalysis.calculateRefinementScore;

public class PartDefTemplateBuilder {

  private final ASTPartDef referencePartDef;
  private final ASTPartDef result;

  public PartDefTemplateBuilder(ASTPartDef referencePartDef) {
    this.result = referencePartDef.deepClone();
    this.referencePartDef = referencePartDef;
  }

  public PartDefTemplateBuilder setName(String name) {
    result.setName(name);
    return this;
  }

  public PartDefTemplateBuilder transformPartUsages(ASTSysMLReqType targetType) {
    if (targetType != ASTSysMLReqType.HLR && targetType != ASTSysMLReqType.LLR) {
      throw new UnsupportedOperationException("Can only convert part usages to HLR or LLR");
    }
    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> {
      for (var p : List.copyOf(result.getSysMLElementList())) {
        if (p instanceof ASTPartUsage){
          try {
            var originalPartDefOpt = scope.resolvePartDef(((ASTPartUsage) p).getSpecializationList().get(0).getSuperTypes(0).printType());
            if (originalPartDefOpt.isEmpty()){
              // When we can't find the original symbol. We can also not check for corresponding HLRs.
              continue;
            }
            var originalPartDef = originalPartDefOpt.get();
            if (originalPartDef.getRequirementType() == targetType){
              // Nothing to do here.
              continue;
            }

            var candidates = targetType == ASTSysMLReqType.HLR
                ? originalPartDef.getRefinements(scope)
                : originalPartDef.getRefiners(scope);

            if (candidates.isEmpty()){
              continue;
            }

            candidates = candidates.stream()
                .sorted(Comparator.comparingInt(candidate -> calculateRefinementScore(candidate, originalPartDef)))
                .collect(Collectors.toList());

            var chosenCandidate = candidates.get(candidates.size() - 1);
            // TODO joiner Joiners.DOT.toString()
            var hlrEquivalentQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(
                List.of(chosenCandidate.getFullName().split("\\."))).build();
            var hlrEquivalentQMcType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(hlrEquivalentQName).build();

            ((ASTPartUsage) p).getSpecializationList().get(0).getSuperTypesList().set(0, hlrEquivalentQMcType);
          } catch (Exception e){
            // continue
          }
        }
      }
    });
    return this;
  }

  public PartDefTemplateBuilder removeProperties(ASTSysMLReqType propertyType) {
    if (propertyType == ASTSysMLReqType.LLR){
      result.removeIfSysMLElement(e -> e instanceof ASTStateUsage);
    }

    if (propertyType == ASTSysMLReqType.HLR){
      result.removeIfSysMLElement(e -> e instanceof ASTRequirementUsage);
      result.removeIfSysMLElement(e -> e instanceof ASTConstraintUsage);
    }

    return this;
  }

  public PartDefTemplateBuilder ifReference(Predicate<ASTPartDef> predicate, Function<PartDefTemplateBuilder, PartDefTemplateBuilder> func){
    if (predicate.test(referencePartDef)){
      return func.apply(this);
    } else {
      return this;
    }
  }

  public PartDefTemplateBuilder addEmptyConstraint(String name) {
    var fillOutComment = new Comment();
    fillOutComment.setText("/* TODO: Fill out */");

    var expr = new ASTLiteralExpressionBuilder().setLiteral(new ASTBooleanLiteralBuilder()
            .setSource(3) // 3 represents true
            .add_PreComment(fillOutComment)
            .build())
        .build();

    var constraint = new ASTConstraintUsageBuilder()
        .setName(name)
        .setRequire(true)
        .setExpression(expr)
        .build();

    var newRequirement = new ASTRequirementUsageBuilder()
        .setName(name)
        .addSysMLElement(constraint)
        .setSatisfy(true).build();

    result.getSysMLElementList().add(newRequirement);
    return this;
  }

  public PartDefTemplateBuilder addEmptyStateUsage(String name) {
    var fillOutComment = new Comment();
    fillOutComment.setText("/* TODO: Fill out */");

    var newStateUsage = new ASTStateUsageBuilder()
        .setName(name)
        .setModifier(SysMLv2Mill.modifierBuilder().build())
        .setDefaultValue(SysMLv2Mill.defaultValueBuilder().build())
        .add_PostComment(fillOutComment)
        .setExhibited(true)
        .build();

    result.getSysMLElementList().add(newStateUsage);
    return this;
  }

  public PartDefTemplateBuilder addDecomposition(ASTPartDef comp1, ASTPartDef comp2) {
    if (result.getSysMLElements(ASTPortUsage.class).isEmpty()){
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

    var referenceInputs = referencePartDef.getSysMLElements(ASTPortUsage.class).stream()
        .filter(p -> estimatePortDirection(p) == ASTSysMLFeatureDirection.IN)
        .collect(Collectors.toList());

    for (ASTPortUsage input : referenceInputs){
      var sourceQNameBuilder = SysMLv2Mill.mCQualifiedNameBuilder();
      sourceQNameBuilder.addParts(input.getName());

      var targetQNameBuilder = SysMLv2Mill.mCQualifiedNameBuilder();
      targetQNameBuilder.addParts("/* TODO: add target */");

      var connectionUsage = SysMLv2Mill.connectionUsageBuilder()
          .setSrc(sourceQNameBuilder.build())
          .setTgt(targetQNameBuilder.build())
          .build();

      result.getSysMLElementList().add(connectionUsage);
    }


    var referenceOutputs = referencePartDef.getSysMLElements(ASTPortUsage.class).stream()
        .filter(p -> estimatePortDirection(p) == ASTSysMLFeatureDirection.OUT)
        .collect(Collectors.toList());

    for (ASTPortUsage output : referenceOutputs){
      var sourceQNameBuilder = SysMLv2Mill.mCQualifiedNameBuilder();
      sourceQNameBuilder.addParts("/* TODO: add source */");


      var targetQNameBuilder = SysMLv2Mill.mCQualifiedNameBuilder();
      targetQNameBuilder.addParts(output.getName());

      var connectionUsage = SysMLv2Mill.connectionUsageBuilder()
          .setSrc(sourceQNameBuilder.build())
          .setTgt(targetQNameBuilder.build())
          .build();

      result.getSysMLElementList().add(connectionUsage);
    }

    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public PartDefTemplateBuilder addPartUsage(String usageName, ASTPartDef partDef){
    var partDefQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(List.of(partDef.getName().split("\\."))).build();
    var usageType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(partDefQName).build();
    result.addSysMLElement(SysMLv2Mill.partUsageBuilder()
        .setModifier(SysMLv2Mill.modifierBuilder().build())
        .setName(usageName)
        .addSpecialization(SysMLv2Mill.sysMLTypingBuilder().addSuperTypes(usageType).build())
        .build());

    return this;
  }

  public PartDefTemplateBuilder addRefinement(ASTPartDef partDef) {
    var partDefQName = SysMLv2Mill.mCQualifiedNameBuilder().addAllParts(List.of(partDef.getName().split("\\."))).build();
    var refinementType = new ASTMCQualifiedTypeBuilder().setMCQualifiedName(partDefQName).build();
    var refinement = SysMLv2Mill.sysMLRefinementBuilder().addSuperTypes(refinementType).build();
    result.addSpecialization(refinement);
    return this;
  }

  public ASTPartDef build() {
    return result;
  }
}
