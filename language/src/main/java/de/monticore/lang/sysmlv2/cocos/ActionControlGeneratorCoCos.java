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
    var merge = elementList.stream().filter(t->t instanceof ASTMergeAction).count();
    var decide = elementList.stream().filter(t->t instanceof ASTDecideAction).count();
    var fork = elementList.stream().filter(t->t instanceof ASTForkAction).count();
    var join = elementList.stream().filter(t->t instanceof ASTJoinAction).count();
    if(merge>1 || decide>1|| fork>1 || join >1)         Log.error("The parent of " + node.getName()
        + " has more than 1 merge, decide, fork or join, this is not allowed");
    if((merge == 1 || decide == 1) && !(merge ==1 &&decide ==1))         Log.error("The parent of " + node.getName()
        + " has a merge/decide node without the corresponding node.");
    if((fork == 1 || join == 1) && !(fork ==1 &&join ==1))         Log.error("The parent of " + node.getName()
        + " has a fork/join node without the corresponding node.");
    if(merge+decide+fork+join>2)         Log.error("The parent of " + node.getName()
        + " can have either no control nodes,1 merge and 1 decide or 1 fork and 1 join.");
  }

}
