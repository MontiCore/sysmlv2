/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlstates.cocos;

import de.monticore.lang.sysmlstates._ast.ASTExitAction;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTExitActionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * MontiBelle has not semantics for ExitActions.
 */
public class NoExitActions implements SysMLStatesASTExitActionCoCo {

  @Override
  public void check(ASTExitAction node) {
    Log.error("0xMB007 ExitActions are not supported.");
  }

}
