/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.*;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SuccessionReachabilityGeneratorCoCos
    implements SysMLActionsASTActionDefCoCo, SysMLActionsASTActionUsageCoCo {

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
    if(!(node instanceof ASTJoinAction || node instanceof ASTDecideAction || node instanceof ASTForkAction
        || node instanceof ASTMergeAction || node instanceof ASTLoopAction || node instanceof ASTSendActionUsage)) {
      checkReachability(node);
    }
  }

  void checkReachability(ASTActionUsage node) {
    var successionList = node.streamSysMLElements().filter(t -> t instanceof ASTSysMLSuccession).map(
        t -> (ASTSysMLSuccession) t).collect(
        Collectors.toList());
    if(!checkReachabilityOfNode("start", successionList,new HashSet<>())) {
      Log.error("ActionUsage " + node.getName() + " has a succession that does not reach \"done\".");

    }
  }

  boolean checkReachabilityOfNode(String source, List<ASTSysMLSuccession> successionList, Set<String> visited) {
    if(!visited.contains(source)){
    if(source.equals("done")) {
      return true;
    }
    else {
      var successors = successionList.stream().filter(t -> t.getSrc().equals(source)).collect(Collectors.toList());
      if(successors.isEmpty()) {
        return false;
      }
      else {
        visited.add(source);
        return successors.stream()
            .allMatch(
                t -> checkReachabilityOfNode(t.getTgt(), successionList, visited));
      }
    }
  }
  return true;
  }

}
