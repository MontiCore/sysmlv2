package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementBody;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTSysMLAssumption;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * The following CoCo verifies that there is no assert constraint usage present in a requirement.
 */
public class AssertConstraintNotAllowedInRequirement implements SysMLRequirementDiagramsASTRequirementBodyCoCo {
  @Override
  public void check(ASTRequirementBody node) {
    List<ASTSysMLAssumption> constraints = node.getSysMLAssumptionList();
    for(ASTSysMLAssumption constraint: constraints) {
      if(constraint.getConstraintUsage().isAssert()) {
        Log.error("Asserting constraints is not allowed in a requirement!",
            constraint.getConstraintUsage().get_SourcePositionStart(),
            constraint.getConstraintUsage().get_SourcePositionEnd());
      }
    }
  }
}
