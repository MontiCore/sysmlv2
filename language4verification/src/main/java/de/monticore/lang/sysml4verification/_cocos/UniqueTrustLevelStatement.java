package de.monticore.lang.sysml4verification._cocos;

import de.monticore.lang.sysml4verificationblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysml4verificationblockdiagrams._cocos.SysML4VerificationBlockDiagramsASTPartDefCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo that ensures that only trust level is defined for a part def.
 */
public class UniqueTrustLevelStatement implements SysML4VerificationBlockDiagramsASTPartDefCoCo {

    @Override
  public void check(ASTPartDef node) {
    if(node.getTrustLevelList().size() > 1) {
      Log.error("There is more than one trust level statement in part def "+node.getName()+" but only one per part "
          + "def is allowed.");
    }
  }
}
