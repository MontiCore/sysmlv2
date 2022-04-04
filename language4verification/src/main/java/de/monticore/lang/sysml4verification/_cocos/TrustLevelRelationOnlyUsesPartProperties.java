package de.monticore.lang.sysml4verification._cocos;

import de.monticore.lang.sysml4verificationblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysml4verificationblockdiagrams._ast.ASTTrustLevelRelation;
import de.monticore.lang.sysml4verificationblockdiagrams._cocos.SysML4VerificationBlockDiagramsASTPartDefCoCo;
import de.monticore.lang.sysmlbasis._symboltable.SysMLUsageSymbol;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartPropertySymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * CoCo that ensures only part properties are used in trust level relation definitions.
 */
public class TrustLevelRelationOnlyUsesPartProperties implements SysML4VerificationBlockDiagramsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {

    for (ASTTrustLevelRelation trustLevelRelation : node.getTrustLevelRelationList()) {

      checkReferenceForPartUsage(node, trustLevelRelation.getLeft());
      checkReferenceForPartUsage(node, trustLevelRelation.getRight());
    }
  }

  private void checkReferenceForPartUsage(ASTPartDef node, String partPropertyName) {
    Optional<SysMLUsageSymbol> symb = node.getSpannedScope().resolveSysMLUsage(partPropertyName);

    if(symb.isPresent()) {
      if(!(symb.get() instanceof PartPropertySymbol)){
        Log.error("Found symbol with name "+partPropertyName+" that is not a part usage. Trust level relations can "
            + "only be defined about part usages.");
      }
    } else {
      Log.error("TL-relations must reference existing part usages. Could not find "+partPropertyName
            + " in the current context.");
    }
  }
}
