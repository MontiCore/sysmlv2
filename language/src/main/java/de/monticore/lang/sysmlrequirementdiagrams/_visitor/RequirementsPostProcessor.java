package de.monticore.lang.sysmlrequirementdiagrams._visitor;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;

/**
 * RequirementsPostProcessor finds and sets:
 * 1. the correct subject in the requirements,
 * 2. the correct requirement parameters in the requirements.
 */
public class RequirementsPostProcessor implements SysMLRequirementDiagramsVisitor2 {

  @Override
  public void visit(ASTRequirementDef node) {
    node.setSubject();
    node.validateRequirementParameters();
  }

  @Override
  public void visit(ASTRequirementUsage node) {
    node.setSubject();
    node.validateRequirementParameters();
  }

}
