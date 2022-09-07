package de.monticore.lang.sysml4verification.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class WarnNonExhibited implements SysMLStatesASTStateUsageCoCo {

  /** Warns about non-exhibited state usages or state usages that are not direct descendants of part definitions */
  @Override
  public void check(ASTStateUsage node) {
    if(!node.isExhibited()) {
      Log.warn("Non-exhibited state usages will be ignored for behavior specification",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
    else if(!parentIsPartDef(node)) {
      Log.warn("Only exhibited state usages within part definition will be considered for behavior specification",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }

  private boolean parentIsPartDef(ASTNode node) {
    return node.getEnclosingScope() != null && node.getEnclosingScope().isPresentAstNode()
        && node.getEnclosingScope().getAstNode() instanceof ASTPartDef;
  }

}
