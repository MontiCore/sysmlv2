package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.se_rwth.commons.logging.Log;

public class CapitalizeDefinitionNamesCoCo implements SysMLPartsASTPartDefCoCo {
  @Override
  public void check(ASTPartDef node) {
    if(node.getName().substring(0,1).toLowerCase()
        .equals(node.getName().substring(0,1))) {
      Log.warn("0xB0B01 Definitions should be capitalized",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
