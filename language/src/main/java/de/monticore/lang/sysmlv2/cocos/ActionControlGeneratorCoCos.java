package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlactions._ast.*;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.generator.helper.ActionsHelper;
import de.monticore.lang.sysmlv2.visitor.ExpressionListResolver;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ActionControlGeneratorCoCos implements SysMLActionsASTActionUsageCoCo {
  /**
   * Check that an action usage has only 1 incoming/outgoing transition. Only Fork, Decide, Merge and Join can have more.
   */
  @Override
  public void check(ASTActionUsage node) {
    ActionsHelper actionsHelper = new ActionsHelper();
    List<ASTSysMLElement> elementList = node.getSysMLElementList();

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
    var merge = elementList.stream().filter(t -> t instanceof ASTMergeAction).count();
    var decide = elementList.stream().filter(t -> t instanceof ASTDecideAction).count();
    var fork = elementList.stream().filter(t -> t instanceof ASTForkAction).count();
    var join = elementList.stream().filter(t -> t instanceof ASTJoinAction).count();
    if(merge > 1 || decide > 1 || fork > 1 || join > 1)
      Log.error("The parent of " + node.getName()
          + " has more than 1 merge, decide, fork or join, this is not allowed");
    if((merge == 1 || decide == 1) && !(merge == 1 && decide == 1))
      Log.error("The parent of " + node.getName()
          + " has a merge/decide node without the corresponding node.");
    if((fork == 1 || join == 1) && !(fork == 1 && join == 1))
      Log.error("The parent of " + node.getName()
          + " has a fork/join node without the corresponding node.");
    if(merge + decide + fork + join > 2)
      Log.error("The parent of " + node.getName()
          + " can have either no control nodes,1 merge and 1 decide or 1 fork and 1 join.");
    if(fork == 1 && join == 1) {
      var firstControlNode = actionsHelper.getFirstControlNode(node);
      var secondControlNode = actionsHelper.getSecondControlNode(node);
      checkPaths(actionsHelper.getDecisionPaths(firstControlNode, secondControlNode));
    }
  }

  void checkPaths(List<List<ASTSysMLSuccession>> forkPaths) {
    var attributeListOfLists = forkPaths.stream().map(this::getAttributeListOfPath).collect(Collectors.toList());
    if(attributeListOfLists.size()>=2){
      var current = new HashSet<>();
      for (List<ASTAttributeUsage> list:
           attributeListOfLists) {
        if(current.isEmpty()){
          current.addAll(list);
        }else{
          var old_current = new HashSet<>(current);
          if(current.retainAll(list)){

            old_current.addAll(list);
            current=old_current;
          }else{
            Log.error("The paths following the fork use attributes that intersect, this is not allowed.");
          }
        }
      }
    }
  }

  List<ASTAttributeUsage> getAttributeListOfPath(List<ASTSysMLSuccession> path) {
    var expressions = path.stream().filter(ASTSysMLSuccession::isPresentGuard).map(ASTSysMLSuccession::getGuard).collect(Collectors.toList());
    final SysMLv2Traverser sysMLv2Traverser = SysMLv2Mill.traverser();
    final ExpressionListResolver resolver = new ExpressionListResolver();
    List<ASTAttributeUsage> AttributeList = new ArrayList<>();
    sysMLv2Traverser.add4ExpressionsBasis(resolver);
    for (ASTExpression expression : expressions
    ) {

      expression.accept(sysMLv2Traverser);
      AttributeList.addAll(resolver.getAttributeList());
    }
    var actionPath = path.stream().map(
        t -> t.getEnclosingScope().resolveActionUsage(t.getTgt()).get().getAstNode()).collect(Collectors.toList());
    var actionExpressions = actionPath.stream().flatMap(t->getExpressionsOfAction(t).stream()).collect(Collectors.toList());
    for (ASTExpression expression : actionExpressions
    ) {
      expression.accept(sysMLv2Traverser);
      AttributeList.addAll(resolver.getAttributeList());
    }
    return AttributeList;
  }

  List<ASTExpression> getExpressionsOfAction(ASTActionUsage node) {
    var subActions = node.getSysMLElementList().stream().filter(t -> t instanceof ASTActionUsage).map(
        t -> (ASTActionUsage) t).collect(
        Collectors.toList());
    var guards = node.getSysMLElementList().stream().filter(t -> t instanceof ASTSysMLSuccession).map(
        t -> (ASTSysMLSuccession) t).filter(ASTSysMLSuccession::isPresentGuard).map(ASTSysMLSuccession::getGuard).collect(
        Collectors.toList());
    var attributeDefaultValues = node.getSysMLElementList().stream().filter(t -> t instanceof ASTAttributeUsage).map(
        t -> (ASTAttributeUsage) t).filter(ASTAttributeUsage::isPresentExpression).map(ASTAttributeUsage::getExpression).collect(
        Collectors.toList());
    guards.addAll(attributeDefaultValues);
    guards.addAll(subActions.stream().flatMap(t->getExpressionsOfAction(t).stream()).collect(Collectors.toList()));
    return guards;
  }

}
