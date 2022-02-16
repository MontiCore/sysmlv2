package schrott._cocos;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import schrott._ast.ASTSuperclassingList;
import de.se_rwth.commons.logging.Log;

public class SysML4VerificationRefinementsExistCoCo implements SysML4VerificationASTSuperclassingListCoCo{

  @Override
  public void check(ASTSuperclassingList node) {
    for (ASTQualifiedName refine : node.getRefinesList()) {
      if(!node.getEnclosingScope().resolveSysMLType(refine.getFullQualifiedName()).isPresent()) {
        Log.error("Components to be refined must exist!", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
  }
}
