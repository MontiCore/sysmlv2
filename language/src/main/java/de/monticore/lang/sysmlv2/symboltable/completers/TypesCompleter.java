/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
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
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class TypesCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2, SysMLRequirementsVisitor2
{

  /**
   * Returns type completion for Usages. Bases on types completed in the SpecializationCompleter. We solely store the
   * qualified name as SymTypeExpression using the defining symbol, outside of generic types (require type printing)
   */
  private List<SymTypeExpression> getTypeCompletion(List<ASTSpecialization> specializationList, boolean conjugated) {
    List<SymTypeExpression> typeExpressions = new ArrayList<>();

    for(var specialization: specializationList) {
      if(specialization instanceof ASTSysMLTyping && ((ASTSysMLTyping) specialization).isConjugated() == conjugated) {
        var astTyping = (ASTSysMLTyping) specialization;

        for(var mcType: astTyping.getSuperTypesList()) {
          SymTypeExpression res = null;
          if(mcType instanceof ASTMCGenericType) {
            // We still have to print when the type is generic because the defining symbol does not give info about the
            // instantiation with type arguments
            res = SymTypeExpressionFactory.createTypeExpression(
                mcType.printType(),
                (IBasicSymbolsScope) mcType.getEnclosingScope());
          }
          else if(mcType.getDefiningSymbol().isPresent() && mcType.getDefiningSymbol().get() instanceof TypeSymbol) {
            res = SymTypeExpressionFactory.createTypeExpression((TypeSymbol) mcType.getDefiningSymbol().get());
          }
          else if(mcType.getDefiningSymbol().isEmpty()) {
            Log.warn("Defining symbol for " + mcType + " was not set.");
          }
          else if(!(mcType.getDefiningSymbol().get() instanceof TypeSymbol)) {
            Log.warn("Defining symbol for " + mcType + " is not a TypeSymbol");
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
      // type
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

}
