/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlstates.cocos;

import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTDoActionCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * MontiBelle has not semantics for DoActions.
 */
public class NoDoActions implements SysMLStatesASTStateUsageCoCo, SysMLStatesASTStateDefCoCo {

  @Override
  public void check(ASTStateUsage node) {
    if(node.isPresentDoAction()) {
      Log.error("DoActions are not supported.", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  @Override
  public void check(ASTStateDef node) {
    if(node.isPresentDoAction()) {
      Log.error("DoActions are not supported.", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
