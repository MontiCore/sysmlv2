/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlactions._ast.ASTCalcUsage;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousReference;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._symboltable.AnonymousReferenceSymbol;
import de.monticore.lang.sysmlbasis._symboltable.AnonymousUsageSymbol;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementSubject;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlconstraints._visitor.SysMLConstraintsVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLRedefinition;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCTupleType;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypesCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2,
    SysMLConstraintsVisitor2, SysMLActionsVisitor2
{

  private List<SymTypeExpression> getTypeCompletion(
      List<ASTSpecialization> specializationList,
      boolean conjugated
  ) {
    return getTypeCompletion(specializationList, conjugated,
        Collections.emptyList());
  }

  /**
   * Returns type completion for Usages. Bases on types completed in the SpecializationCompleter. We solely store the
   * qualified name as SymTypeExpression using the defining symbol, outside of generic types (require type printing)
   */
  public static List<SymTypeExpression> getTypeCompletion(
      List<ASTSpecialization> specializationList,
      boolean conjugated,
      List<ASTSysMLElement> bodyElements
  ) {
    List<SymTypeExpression> typeExpressions = new ArrayList<>();

    for(var specialization: specializationList) {
      // TODO Mike: var symType = TypeCheck3.symTypeFromAST(mcType);
      //  Restlichen Code wegwerfen, sobald der TypeCheck soweit ist!
      if(specialization instanceof ASTSysMLTyping && ((ASTSysMLTyping) specialization).isConjugated() == conjugated) {
        var astTyping = (ASTSysMLTyping) specialization;

        for(var mcType: astTyping.getSuperTypesList()) {
          SymTypeExpression res = null;
          if(mcType instanceof ASTMCTupleType) {
            var tupleType = (ASTMCTupleType) mcType;
            List<SymTypeExpression> componentTypes = new ArrayList<>();
            for(var componentMcType : tupleType.getMCTypeList()) {
              componentTypes.add(SymTypeExpressionFactory.createTypeExpression(
                  componentMcType.printType(),
                  (IBasicSymbolsScope) componentMcType.getEnclosingScope()));
            }
            res = SymTypeExpressionFactory.createTuple(componentTypes);
          }
          if(mcType instanceof ASTMCQualifiedType &&
              ((ASTMCQualifiedType)mcType).getNameList().get(
                  ((ASTMCQualifiedType)mcType).getNameList().size()-1
              ).equals("Tuple"))
          {
            // Im Body stehen die Redefinitionen der Argumente. In Java würde
            // man von TypVariablen sprechen, die assignes werden
            // (List<String>). In SysML regelt man das mit "redefines"
            // (redefines element : String;)
            List<ASTAnonymousUsage> anonymousUsages = bodyElements.stream()
                .filter(e -> e instanceof ASTAnonymousUsage)
                .map(e -> (ASTAnonymousUsage) e).collect(Collectors.toList());

            // Typ von erstem Tuple-Argument bestimmen.
            var fstAnonUsage = anonymousUsages.stream().filter(e -> e.getSpecializationList().stream()
                .filter(s -> s instanceof ASTSysMLRedefinition)
                .map(s -> (ASTSysMLRedefinition) s)
                .anyMatch(s -> s.getSuperTypesList().stream()
                    .anyMatch(t -> t.printType().equals("fst")))
            );

            List<ASTSysMLTyping> fstSysMLTyping = fstAnonUsage
                .map(s -> s.getSpecializationList())
                .flatMap(s -> s.stream())
                .filter(s -> s instanceof ASTSysMLTyping)
                .map(s -> (ASTSysMLTyping) s).collect(Collectors.toList());

            SymTypeExpression fst = fstSysMLTyping.stream()
                .map(s -> s.getSuperTypesList())
                .flatMap(s -> s.stream())
                .map(t -> SymTypeExpressionFactory.createTypeExpression(
                    t.printType(), (IBasicSymbolsScope) t.getEnclosingScope()))
                .collect(Collectors.toList()).get(0);

            // Typ von zweitem Tuple-Argument bestimmen.
            var sndAnonUsage = anonymousUsages.stream().filter(e -> e.getSpecializationList().stream()
                .filter(s -> s instanceof ASTSysMLRedefinition)
                .map(s -> (ASTSysMLRedefinition) s)
                .anyMatch(s -> s.getSuperTypesList().stream()
                    .anyMatch(t -> t.printType().equals("snd")))
            );

            List<ASTSysMLTyping> sndSysMLTyping = sndAnonUsage
                .map(s -> s.getSpecializationList())
                .flatMap(s -> s.stream())
                .filter(s -> s instanceof ASTSysMLTyping)
                .map(s -> (ASTSysMLTyping) s).collect(Collectors.toList());

            SymTypeExpression snd = sndSysMLTyping.stream()
                .map(s -> s.getSuperTypesList())
                .flatMap(s -> s.stream())
                .map(t -> SymTypeExpressionFactory.createTypeExpression(
                    t.printType(), (IBasicSymbolsScope) t.getEnclosingScope()))
                .collect(Collectors.toList()).get(0);

            res = SymTypeExpressionFactory.createTuple(fst,snd);


          }
          else if(mcType instanceof ASTMCGenericType) {
            // We still have to print when the type is generic because the defining symbol does not give info about the
            // instantiation with type arguments
            res = SymTypeExpressionFactory.createTypeExpression(
                mcType.printType(),
                (IBasicSymbolsScope) mcType.getEnclosingScope());
          }
          else if(mcType.getDefiningSymbol().isPresent() && mcType.getDefiningSymbol().get() instanceof TypeSymbol) {
            // hacky setup such that nat remains a primitive
            if (mcType.getDefiningSymbol().get().getName().equals("nat")) {
              res = SymTypeExpressionFactory.createPrimitive((TypeSymbol) mcType.getDefiningSymbol().get());
            } else {
              res = SymTypeExpressionFactory.createTypeExpression((TypeSymbol) mcType.getDefiningSymbol().get());
            }
          }
          else if(mcType.getDefiningSymbol().isEmpty()) {
            Log.warn("Defining symbol for " + mcType.printType() + " was not set.");
          }
          else if(!(mcType.getDefiningSymbol().get() instanceof TypeSymbol)) {
            Log.warn("Defining symbol for " + mcType.printType() + " is not a TypeSymbol");
          }

          if(res != null) {
            if(astTyping.isPresentCardinality()) {
              res = SymTypeExpressionFactory.createTypeArray(res.getTypeInfo(), 1, res);
            }
            typeExpressions.add(res);
          }
        }
      }
    }
    return typeExpressions;
  }

  @Override
  public void visit(ASTSysMLParameter node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

    if(node.isPresentSymbol() && !types.isEmpty()) {
      node.getSymbol().setType(types.get(0));
    }
  }

  /**
   * Completes the usage symbol with corresponding types used by further model-processing tools. Type is stored as a
   * SymTypeExpression and requires a backing Type symbol set by a SpecializationCompleter
   */
  @Override
  public void visit(ASTPartUsage node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

    if(node.isPresentSymbol()) {
      node.getSymbol().setTypesList(types);
    }
  }

  /**
   * See {@link  TypesCompleter#visit(ASTPartUsage)}
   */
  @Override
  public void visit(ASTPortUsage node) {
    if(node.isPresentSymbol()) {
      PortUsageSymbol symbol = node.getSymbol();

      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);
      List<SymTypeExpression> conjugatedTypes = getTypeCompletion(node.getSpecializationList(), true);

      symbol.setTypesList(types);
      symbol.setConjugatedTypesList(conjugatedTypes);
    }
  }

  /**
   * See {@link  TypesCompleter#visit(ASTPartUsage)}
   */
  @Override
  public void visit(ASTAttributeUsage node) {
    if(node.isPresentSymbol()) {
      AttributeUsageSymbol symbol = node.getSymbol();
      // Hier könnte der Typ "Tuple<Boolean,...> sein. Das "Boolean" steht dabei
      // als Redefinition im Body. Deswegen geben wor die SysMLElements weiter.
      List<SymTypeExpression> types = getTypeCompletion(
          node.getSpecializationList(),
          false,
          node.getSysMLElementList()
      );

      symbol.setAccessModifier(BasicAccessModifier.ALL_INCLUSION);

      symbol.setTypesList(types);
    }
  }

  @Override
  public void endVisit(ASTCalcUsage node) {
    if (node.isPresentSymbol()) {
      // Use a one-element array because Java does not allow reassigning local variables
      // from inside anonymous visitor classes
      final SymTypeExpression[] returnType = new SymTypeExpression[1];
      returnType[0] = SymTypeExpressionFactory.createTopType();

      var traverser = SysMLv2Mill.inheritanceTraverser();
      traverser.add4SysMLBasis(new SysMLBasisVisitor2() {
        @Override
        public void visit(ASTAnonymousUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isReturn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            returnType[0] = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
          }
        }
      });

      traverser.add4SysMLParts(new SysMLPartsVisitor2() {
        @Override
        public void visit(ASTAttributeUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isReturn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            returnType[0] = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
          }
        }
      });
      node.accept(traverser);
      node.getSymbol().setReturnType(returnType[0]);

      List<SymTypeExpression> argTypes = new ArrayList<>();
      var argTraverser = SysMLv2Mill.inheritanceTraverser();
      argTraverser.add4SysMLBasis(new SysMLBasisVisitor2() {
        @Override
        public void visit(ASTAnonymousUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isIn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            var argType = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
            argTypes.add(argType);
          }
        }
      });

      argTraverser.add4SysMLParts(new SysMLPartsVisitor2() {
        @Override
        public void visit(ASTAttributeUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isIn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            var argType = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
            argTypes.add(argType);
          }
        }
      });
      node.accept(argTraverser);
      node.getSymbol().setArgTypes(argTypes);
    }
  }

  @Override
  public void endVisit(ASTCalcDef node) {
    if (node.isPresentSymbol()) {
      // Use a one-element array because Java does not allow reassigning local variables
      // from inside anonymous visitor classes
      final SymTypeExpression[] returnType = new SymTypeExpression[1];
      returnType[0] = SymTypeExpressionFactory.createTopType();

      var traverser = SysMLv2Mill.inheritanceTraverser();
      traverser.add4SysMLBasis(new SysMLBasisVisitor2() {
        @Override
        public void visit(ASTAnonymousUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isReturn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            returnType[0] = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
          }
        }
      });

      traverser.add4SysMLParts(new SysMLPartsVisitor2() {
        @Override
        public void visit(ASTAttributeUsage retNode) {
          var modifier = retNode.getModifier();
          if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier
              && ((de.monticore.lang.sysmlv2._ast.ASTModifier) modifier).isReturn()
              && retNode.getEnclosingScope() == node.getSpannedScope()) {
            List<SymTypeExpression> types = getTypeCompletion(retNode.getSpecializationList(), false);
            returnType[0] = types.isEmpty() ? SymTypeExpressionFactory.createObscureType() : types.get(0);
          }
        }
      });
      node.accept(traverser);
      node.getSymbol().setReturnType(returnType[0]);
    }
  }

  @Override
  public void endVisit(ASTAnonymousUsage node) {
    if(node.isPresentSymbol()) {
      AnonymousUsageSymbol symbol = node.getSymbol();
      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);
      symbol.setTypesList(types);
    }
  }

  @Override
  public void visit(ASTRequirementSubject node) {
    if(node.isPresentSymbol()) {
      RequirementSubjectSymbol symbol = node.getSymbol();
      // type
      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

      symbol.setTypesList(types);
    }
  }

  @Override
  public void visit(ASTAnonymousReference node) {
    if(node.isPresentSymbol()) {
      AnonymousReferenceSymbol symbol = node.getSymbol();
      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);
      symbol.setTypesList(types);
    }
  }
}
