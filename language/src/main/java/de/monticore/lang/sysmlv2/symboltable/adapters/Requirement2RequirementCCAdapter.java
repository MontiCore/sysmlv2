/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementSubject;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;

import java.util.List;

public class Requirement2RequirementCCAdapter extends RequirementSymbol  {

  private RequirementUsageSymbol adaptee;

  public Requirement2RequirementCCAdapter(RequirementUsageSymbol requirementUsageSymbol) {
    super(requirementUsageSymbol.getFullName());
    this.adaptee = requirementUsageSymbol;
  }

  public MildComponentSymbol getSubject() {
    var subject = this.adaptee.getAstNode().getSysMLElementList()
        .stream()
        .filter(e -> e instanceof ASTRequirementSubject)
        .map(e -> (ASTRequirementSubject) e)
        .map(s -> s.getSymbol())
        .findFirst().orElse(null);

    if(subject.getTypesList().size() != 1) {
      return null;
    }

    var subjectTypeName = subject.getTypes(0).printFullName();
    return ((SysMLv2Scope) this.adaptee.getEnclosingScope())
                .resolveMildComponent(subjectTypeName)
                .orElse(null);
  }

  @Override
  public List<ASTExpression> getAssumptionsList() {
    return this.adaptee.getAstNode().getSysMLElementList()
        .stream()
        .filter(e -> e instanceof ASTConstraintUsage)
        .map(e -> (ASTConstraintUsage) e)
        .filter(usage -> usage.isAssume())
        .map(ASTConstraintUsage::getExpression)
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public ASTExpression getGuarantee() {
    return this.adaptee.getAstNode().getSysMLElementList()
        .stream()
        .filter(e -> e instanceof ASTConstraintUsage)
        .map(e -> (ASTConstraintUsage) e)
        .filter(usage -> usage.isAssert())
        .map(ASTConstraintUsage::getExpression)
        .findFirst().orElse(null);
  }
}
