/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class StateNameCoCos implements SysMLStatesASTStateDefCoCo, SysMLStatesASTStateUsageCoCo {

  /**
   * Check that all super types (specializations) exist. They need to be Action definitions.
   */
  @Override
  public void check(ASTStateDef node) {

    if(node.getName().equals("done")) {
      Log.error("State definition can not have the name \" done\".");
    }
    if(node.getName().equals("start")) {
      Log.error("State definition can not have the name \" first\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be Action definitions or usages.
   */
  @Override
  public void check(ASTStateUsage node) {
    if(node.getName().equals("done")) {
      Log.error("State usage can not have the name \" done\".");
    }
    if(node.getName().equals("start")) {
      Log.error("State usage can not have the name \" first\".");
    }
  }
}
