package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.se_rwth.commons.logging.Log;

public class StateExistsCoCo implements SysMLStatesASTSysMLTransitionCoCo {
  @Override
  public void check (ASTSysMLTransition node){
    ISysMLStatesScope scope = node.getEnclosingScope();
    boolean isSrcPresent = scope.resolveStateUsage(node.getSrc()).isPresent();
    boolean isTgtPresent = scope.resolveStateUsage(node.getTgt()).isPresent();

    if(!isSrcPresent){
      Log.error("Source state is not defined", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    if(!isTgtPresent){
      Log.error("Target state is not defined", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}