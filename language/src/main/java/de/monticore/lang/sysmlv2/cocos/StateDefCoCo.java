/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class StateDefCoCo implements SysMLPartsASTPartDefCoCo {
  @Override
  public void check(ASTPartDef node) {
    List<ASTStateUsage> stateDefs = node.getSysMLElements(ASTStateUsage.class);
    if(stateDefs.size() != 1){
      Log.warn("0xA70003 Part " + node.getName() + " must contain exactly one state usage!");
    }
  }
}
