/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._ast.ASTUsageSpecialization;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTEnumDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementSubject;
import de.monticore.lang.sysmlrequirements._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlrequirements._visitor.SysMLRequirementsVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class TypesAndDirectionCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2, SysMLRequirementsVisitor2
{

  /**
   * Returns type completion for Usages. Bases on types completed in the SpecializationCompleter. We solely store the qualified name as
   * SymTypeExpression using the defining symbol, outside of generic types (require type printing)
   */
  private List<SymTypeExpression> getTypeCompletion(List<ASTUsageSpecialization> specializationList, boolean conjugated) {
    List<SymTypeExpression> typeExpressions = new ArrayList<>();

    for(var specialization: specializationList) {
      if(specialization instanceof ASTSysMLTyping && ((ASTSysMLTyping) specialization).isConjugated() == conjugated) {
        var astTyping = (ASTSysMLTyping) specialization;

        for(var mcType: astTyping.getSuperTypesList()) {
          if(mcType instanceof ASTMCGenericType) {
            // We still have to print when the type is generic because the defining symbol does not give info about the instantiation with type arguments
            typeExpressions.add(SymTypeExpressionFactory.createTypeExpression(
                mcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())),
                (IBasicSymbolsScope) mcType.getEnclosingScope()));
          }
          else if(mcType.getDefiningSymbol().isPresent() && mcType.getDefiningSymbol().get() instanceof TypeSymbol) {
            typeExpressions.add(SymTypeExpressionFactory.createTypeExpression((TypeSymbol) mcType.getDefiningSymbol().get()));
          }
          else if(mcType.getDefiningSymbol().isEmpty()) {
            Log.warn("Defining symbol for " + mcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())) + " was not set.");
          }
          else if(!(mcType.getDefiningSymbol().get() instanceof TypeSymbol)) {
            Log.warn("Defining symbol for " + mcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())) + " is not a TypeSymbol");
          }
        }
      }
    }
    return typeExpressions;
  }

  @Override
  public void visit(ASTSysMLParameter node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getUsageSpecializationList(), false);

    if(node.isPresentSymbol() && !types.isEmpty()) {
      node.getSymbol().setType(types.get(0));
    }
  }

  /**
   * Completes the usage symbol with corresponding types used by further model-processing tools. Type is stored as a SymTypeExpression
   * and requires a backing Type symbol set by a SpecializationCompleter
   */
  @Override
  public void visit(ASTPartUsage node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getUsageSpecializationList(), false);

    if(node.isPresentSymbol()) {
      node.getSymbol().setTypesList(types);
    }
  }

  /**
   * See {@link  TypesAndDirectionCompleter#visit(ASTPartUsage)}
   */
  @Override
  public void visit(ASTPortUsage node) {
    if(node.isPresentSymbol()) {
      PortUsageSymbol symbol = node.getSymbol();

      List<SymTypeExpression> types = getTypeCompletion(node.getUsageSpecializationList(), false);
      List<SymTypeExpression> conjugatedTypes = getTypeCompletion(node.getUsageSpecializationList(), true);

      symbol.setTypesList(types);
      symbol.setConjugatedTypesList(conjugatedTypes);
    }
  }

  /**
   * See {@link  TypesAndDirectionCompleter#visit(ASTPartUsage)}
   */
  @Override
  public void visit(ASTAttributeUsage node) {
    if(node.isPresentSymbol()) {
      AttributeUsageSymbol symbol = node.getSymbol();
      // type
      List<SymTypeExpression> types = getTypeCompletion(node.getUsageSpecializationList(), false);

      symbol.setTypesList(types);

      // feature direction
      if(node.isPresentSysMLFeatureDirection()) {
        symbol.setDirection(node.getSysMLFeatureDirection());
      }
      else {
        symbol.setDirection(ASTSysMLFeatureDirection.IN);
      }
    }
  }

  @Override
  public void visit(ASTRequirementSubject node) {
    if(node.isPresentSymbol()) {
      RequirementSubjectSymbol symbol = node.getSymbol();
      // type
      List<SymTypeExpression> types = getTypeCompletion(node.getUsageSpecializationList(), false);

      symbol.setTypesList(types);
    }
  }

  @Override
  public void visit(ASTEnumDef node) {
    for(var enumConst : node.getSysMLEnumConstantList()) {
      if(enumConst.isPresentSymbol()) {
        enumConst.getSymbol().setType(SymTypeExpressionFactory.createTypeObject(node.getName(), node.getEnclosingScope()));
      }
    }
  }
}
