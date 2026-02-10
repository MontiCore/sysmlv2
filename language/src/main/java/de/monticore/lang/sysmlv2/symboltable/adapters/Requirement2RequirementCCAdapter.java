/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;

public class Requirement2RequirementCCAdapter extends RequirementSymbol  {
  private ASTRequirementUsage astNode;

  public Requirement2RequirementCCAdapter(String name,
                                          ASTRequirementUsage astNode) {
    super(name);
    this.astNode = astNode;
  }

  public ASTRequirementUsage getAstNode2() {
    return astNode;
  }
}
