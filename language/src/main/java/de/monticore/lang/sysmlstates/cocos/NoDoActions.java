package de.monticore.lang.sysmlstates.cocos;

import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTDoActionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * MontiBelle has not semantics for DoActions.
 */
public class NoDoActions implements SysMLStatesASTDoActionCoCo {

  @Override
  public void check(ASTDoAction node) {
    Log.error("DoActions are not supported.");
  }

}
