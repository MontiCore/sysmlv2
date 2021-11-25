package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementSubject;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo verifies that a requirement subject must always have a type, whether:
 * 1. typed by some MCType, e.g. subject vehicle: Vehicle;
 * 2  given by an expression, e.g. subject vehicle: vehicleContext.vehicle;
 */
public class RequirementSubjectMustExist implements SysMLRequirementDiagramsASTRequirementSubjectCoCo {

  @Override
  public void check(ASTRequirementSubject node) {
    if (node.getSymbol().getSubjectType() == null) {
      Log.error("Type of requirement subject could not be resolved!",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
