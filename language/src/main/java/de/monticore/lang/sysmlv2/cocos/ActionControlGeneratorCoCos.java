package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.*;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ActionControlGeneratorCoCos implements SysMLActionsASTActionUsageCoCo {
  /**
   * Check that an action usage has only 1 incoming/outgoing transition. Only Fork, Decide, Merge and Join can have more.
   */
  @Override
  public void check(ASTActionUsage node) {

    var parent = node.getEnclosingScope().getAstNode();
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(parent instanceof ASTActionUsage)
      elementList = ((ASTActionUsage) parent).getSysMLElementList();

    if(parent instanceof ASTActionDef)
      elementList = ((ASTActionDef) parent).getSysMLElementList();

    long thisElementAsSource = elementList.stream().filter(t -> t instanceof ASTSysMLSuccession).map(
        s -> ((ASTSysMLSuccession) s).getSrc()).filter(t -> t.equals(node.getName())).count();
    long thisElementAsTgt = elementList.stream().filter(t -> t instanceof ASTSysMLSuccession).map(
        s -> ((ASTSysMLSuccession) s).getTgt()).filter(t -> t.equals(node.getName())).count();

    if(thisElementAsSource > 1) {
      if(!(node instanceof ASTForkAction) && !(node instanceof ASTDecideAction)) {
        Log.error("Action usage " + node.getName()
            + " has more than 1 outgoing successions without being a fork/decide action.");
      }
    }
    if(thisElementAsTgt > 1) {

      if(!(node instanceof ASTJoinAction) && !(node instanceof ASTMergeAction)) {
        Log.error("Action usage " + node.getName()
            + " has more than 1 incoming successions without being a join/merge action.");
      }
    }
  }

}
