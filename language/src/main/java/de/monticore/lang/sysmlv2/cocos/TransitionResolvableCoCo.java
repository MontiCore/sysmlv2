/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.se_rwth.commons.logging.Log;

public class TransitionResolvableCoCo implements SysMLStatesASTSysMLTransitionCoCo {

  /**
   * Check that target and source are resolvable as state usages
   */

  @Override
  public void check(ASTSysMLTransition node) {
    if(node.getEnclosingScope().resolveStateUsage(node.getSrc()).isEmpty())
      Log.error("Transition can not resolve the source state usage \"" + node.getSrc() + "\" .");
    if(node.getEnclosingScope().resolveStateUsage(node.getTgt()).isEmpty())
      Log.error("Transition can not resolve the target state usage \"" + node.getTgt() + "\" .");
  }
}
