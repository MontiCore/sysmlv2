package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementBody;
import de.se_rwth.commons.logging.Log;

/**
 * The following CoCo verifies that there is at most one requirement subject in requirement.
 */
public class AtMostSingleSubjectInRequirement implements SysMLRequirementDiagramsASTRequirementBodyCoCo {
  @Override
  public void check(ASTRequirementBody node) {
    if(node.getRequirementSubjectList().size() > 1
            || node.getEnclosingScope().getRequirementSubjectSymbols().values().size() > 1) {
      Log.error("Multiple requirement subjects found. At most one subject is allowed in requirement!");
    }
  }
}
