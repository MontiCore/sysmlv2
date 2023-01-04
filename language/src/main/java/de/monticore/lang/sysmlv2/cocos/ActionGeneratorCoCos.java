/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTDecideAction;
import de.monticore.lang.sysmlactions._ast.ASTForkAction;
import de.monticore.lang.sysmlactions._ast.ASTJoinAction;
import de.monticore.lang.sysmlactions._ast.ASTLoopAction;
import de.monticore.lang.sysmlactions._ast.ASTMergeAction;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.se_rwth.commons.logging.Log;

public class ActionGeneratorCoCos implements SysMLActionsASTActionDefCoCo, SysMLActionsASTActionUsageCoCo {

  /**
   * Check that all super types (specializations) exist. They need to be Action definitions.
   */
  @Override
  public void check(ASTActionDef node) {

    //
  }

  /**
   * Check that all super types (specializations) exist. They might be Action definitions or usages.
   */
  @Override
  public void check(ASTActionUsage node) {
    int firstCount = 0;
    if(!(node instanceof ASTJoinAction || node instanceof ASTDecideAction || node instanceof ASTForkAction
        || node instanceof ASTMergeAction || node instanceof ASTLoopAction || node instanceof ASTSendActionUsage)) {
      for (ASTSysMLElement x : node.getSysMLElementList()) {
        if(x instanceof ASTSysMLFirst) {
          firstCount++;
          if (!((ASTSysMLFirst) x).getName().equals("start")) {
            Log.error("Action first usage has to use the name \" start\".");
          }
        }
      }
      if(firstCount != 1) {
        Log.error("ActionUsage " + node.getName() + " has " + firstCount + " \"first\" usage, but needs exactly 1.");
      }
    }
  }
}
