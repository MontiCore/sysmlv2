/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class ActionNameCoCos implements SysMLActionsASTActionDefCoCo, SysMLActionsASTActionUsageCoCo {


  /**
   * Check that all super types (specializations) exist. They need to be Action definitions.
   */
  @Override
  public void check(ASTActionDef node) {

    if (node.getName().equals("done")) {
      Log.error("0xFF001 Action definition can not have the name \" done\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be Action definitions or usages.
   */
  @Override
  public void check(ASTActionUsage node) {
    if (node.getName().equals("done")) {
      Log.error("0xFF002 Action definition can not have the name \" done\".");
    }
  }
}
