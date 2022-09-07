package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.se_rwth.commons.logging.Log;

/**
 * Parameters in RequirementDefinitions aren't allowed to have a value (binding).
 */
public class ValuesNotAllowedForRequirementDefParameters
    implements SysMLRequirementDiagramsASTRequirementDefCoCo {

  @Override
  public void check(ASTRequirementDef node) {
    if(node.isPresentParameterList()) {
      for (ASTSysMLParameter param : node.getParameterList().getSysMLParameterList()) {
        if(param.isPresentBinding()) {
          Log.error("RequirementDefinition '" + node.getName() + "' has a parameter "
                  + "'" + param.getName() + "' with a FeatureValue. FeatureValues are not allowed in definitions.",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
  }
}
