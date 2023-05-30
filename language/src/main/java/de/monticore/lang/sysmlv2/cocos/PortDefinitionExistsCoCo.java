/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.List;

public class PortDefinitionExistsCoCo implements SysMLPartsASTPartDefCoCo {
  @Override
  public void check(ASTPartDef node) {
    List<ASTPortUsage> portUsages = node.getSysMLElements(ASTPortUsage.class);
    if(portUsages.isEmpty()) {
      Log.warn("0xA70002 Part " + node.getName() + " must use at least one port!");
    }
  }
}