/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementUsageSymbol;

public class Requirement2RequirementCCAdapter extends RequirementSymbol  {
  private ASTRequirementUsage ccAST;

  public Requirement2RequirementCCAdapter(RequirementUsageSymbol requirementUsageSymbol) {
    super(requirementUsageSymbol.getFullName());
    this.ccAST = requirementUsageSymbol.getAstNode();
  }

  public ASTRequirementUsage getCcAST() {
    return ccAST;
  }
}
