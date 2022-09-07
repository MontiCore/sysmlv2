package de.monticore.lang.sysmlrequirementdiagrams._visitor;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;

/**
 * Visitor adds inherited parameters for parameterized requirements.
 */
public class InheritNonRedefinedParametersVisitor implements SysMLRequirementDiagramsVisitor2 {

  @Override
  public void visit(ASTRequirementDef node) {
    node.inheritNonRedefinedRequirementDefinitionParameters();
  }

  @Override
  public void visit(ASTRequirementUsage node) {
    node.inheritNonRedefinedRequirementUsageParameters();
  }
}
