package schrott._cocos;

import schrott._ast.ASTBlockDeclaration;
import de.se_rwth.commons.logging.Log;


public class SysML4VerificationUpperCaseBlockNameCoCo implements SysML4VerificationASTBlockDeclarationCoCo {

  @Override
  public void check(ASTBlockDeclaration node) {
    if (node.getName().charAt(0) != node.getName().toUpperCase().charAt(0)){
      Log.error("0xSML01 First letter of block definition name must be upper case!", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

}
