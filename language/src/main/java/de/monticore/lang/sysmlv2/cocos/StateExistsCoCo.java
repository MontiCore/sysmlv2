/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.se_rwth.commons.logging.Log;

public class StateExistsCoCo implements SysMLStatesASTSysMLTransitionCoCo {
  @Override
  public void check (ASTSysMLTransition node){
    ISysMLStatesScope scope = node.getEnclosingScope();
    boolean isSrcPresent = scope.resolveStateUsage(node.getSrc().getQName()).isPresent();
    boolean isTgtPresent = scope.resolveStateUsage(node.getSuccessionThen().getMCQualifiedName().getQName()).isPresent();

    if(!isSrcPresent){
      Log.error("0x10029 Source state is not defined", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    if(!isTgtPresent){
      Log.error("0x10030 Target state is not defined", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
