package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTDecideAction;
import de.monticore.lang.sysmlactions._ast.ASTForkAction;
import de.monticore.lang.sysmlactions._ast.ASTJoinAction;
import de.monticore.lang.sysmlactions._ast.ASTMergeAction;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActionsHelper {

  public boolean isSendAction(ASTDoAction doAction) {
    if(doAction.isPresentActionUsage()) {
      return doAction.getActionUsage() instanceof ASTSendActionUsage;
    }
    else if(doAction.isPresentAction()) {
      var actionResolve = doAction.getEnclosingScope().resolveActionUsage(doAction.getAction());
      if(actionResolve.isPresent()) {
        return actionResolve.get().getAstNode() instanceof ASTSendActionUsage;
      }
    }
    return false;
  }

  public ASTSendActionUsage castToSend(ASTDoAction doAction) {
    if(doAction.isPresentActionUsage()) {
      return (ASTSendActionUsage) doAction.getActionUsage();
    }
    else if(doAction.isPresentAction()) {
      var actionResolve = doAction.getEnclosingScope().resolveActionUsage(doAction.getAction());
      if(actionResolve.isPresent())
        return (ASTSendActionUsage) actionResolve.get().getAstNode();

    }
    return null;
  }

  public List<ASTSysMLSuccession> getSuccessions(ASTActionUsage actionUsage) {

    return actionUsage.streamSysMLElements().filter(t -> t instanceof ASTSysMLSuccession).map(
        t -> (ASTSysMLSuccession) t).collect(
        Collectors.toList());
  }

  boolean isControlNode(ASTActionUsage actionUsage) {
    return actionUsage instanceof ASTMergeAction || actionUsage instanceof ASTDecideAction
        || actionUsage instanceof ASTForkAction || actionUsage instanceof ASTJoinAction;
  }

  public boolean isDoneOrControlNode(String actionName, ASTSysMLSuccession sysMLSuccession) {
    if(actionName.equals("done"))
      return true;
    ASTActionUsage actionUsage = sysMLSuccession.getEnclosingScope().resolveActionUsage(actionName).get().getAstNode();
    return actionUsage instanceof ASTMergeAction || actionUsage instanceof ASTDecideAction
        || actionUsage instanceof ASTForkAction || actionUsage instanceof ASTJoinAction;
  }

  public List<ASTSysMLSuccession> getPathFromAction(ASTActionUsage actionUsage,
                                                    List<ASTSysMLSuccession> successionList) {
    List<ASTSysMLSuccession> sysMLSuccessions = new ArrayList<>();
    if(!isControlNode(actionUsage) && !actionUsage.getName().equals("done")) {
      var successionOptionalFirst = successionList.stream().filter(
          t -> t.getSrc().equals(actionUsage.getName())).findFirst();
      sysMLSuccessions.add(successionOptionalFirst.get());
      if(successionOptionalFirst.get().getTgt().equals("done"))
        return sysMLSuccessions;
      ASTActionUsage currentUsage = actionUsage.getEnclosingScope().resolveActionUsage(
          successionOptionalFirst.get().getTgt()).get().getAstNode();
      sysMLSuccessions.addAll(getPathFromAction(currentUsage, successionList));
      return sysMLSuccessions;

    }
    else
      return sysMLSuccessions;
  }

  public List<ASTSysMLSuccession> getPathFromStart(ASTActionUsage actionUsage) {
    var successions = getSuccessions(actionUsage);
    if(!actionUsage.getSysMLElementList().isEmpty()) {
      var successionOptionalFirst = successions.stream().filter(
          t -> t.getSrc().equals("start")).findFirst();
      if(!successionOptionalFirst.get().getTgt().equals("done")) {
        List<ASTSysMLSuccession> returnList = new ArrayList<>(List.of(successionOptionalFirst.get()));
        ASTActionUsage currentUsage = actionUsage.getSpannedScope().resolveActionUsage(
            successionOptionalFirst.get().getTgt()).get().getAstNode();
        returnList.addAll(getPathFromAction(currentUsage, successions));
        return returnList;
      }
      else
        return List.of(successionOptionalFirst.get());
    }
    return new ArrayList<>();
  }

  public List<ASTSysMLSuccession> getEndPath(ASTActionUsage parent) {
    var secondControlNode = getSecondControlNode(parent);
    var successorNode = getDirectSuccessor(secondControlNode);
    var successions = getSuccessions(parent);

    var successionToSuccessor = successions.stream().filter(t -> t.getSrc().equals(secondControlNode.getName())).filter(
        t -> t.getTgt().equals(successorNode.getName())).findFirst();
    List<ASTSysMLSuccession> returnList = new ArrayList<>(List.of(successionToSuccessor.get()));
    returnList.addAll(getPathFromAction(successorNode, successions));
    return returnList;
  }

  public boolean hasActionDecideMerge(ASTActionUsage actionUsage) {
    return actionUsage.streamSysMLElements().anyMatch(t -> t instanceof ASTMergeAction || t instanceof ASTDecideAction);
  }

  public boolean hasActionForkJoin(ASTActionUsage actionUsage) {
    return actionUsage.streamSysMLElements().anyMatch(t -> t instanceof ASTForkAction || t instanceof ASTJoinAction);
  }

  public ASTActionUsage getFirstControlNode(ASTActionUsage actionUsage) {
    var path = getPathFromStart(actionUsage);
    String controlName = path.get(path.size() - 1).getTgt();
    return actionUsage.getSpannedScope().resolveActionUsage(controlName).get().getAstNode();
  }

  public ASTActionUsage getSecondControlNode(ASTActionUsage actionUsage) {
    var firstControlNode = getFirstControlNode(actionUsage);
    var successions = getSuccessions(actionUsage);
    if(firstControlNode instanceof ASTForkAction || firstControlNode instanceof ASTMergeAction
        || firstControlNode instanceof ASTDecideAction) {
      var succFromFirst = successions.stream().filter(
          t -> t.getSrc().equals(firstControlNode.getName())).findFirst().get();
      var successor = actionUsage.getSpannedScope().resolveActionUsage(succFromFirst.getTgt()).get().getAstNode();

      if(successor instanceof ASTForkAction || successor instanceof ASTMergeAction
          || successor instanceof ASTDecideAction || successor instanceof ASTJoinAction)
        return successor;

      var pathToSecond = getPathFromAction(successor, successions);
      String controlName = pathToSecond.get(pathToSecond.size() - 1).getTgt();
      return actionUsage.getSpannedScope().resolveActionUsage(controlName).get().getAstNode();
    }
    return null;
  }

  public ASTActionUsage getDirectSuccessor(ASTActionUsage astStateUsage) {
    return getDirectSuccessorList(astStateUsage).get(0);
  }

  public List<ASTActionUsage> getDirectSuccessorList(ASTActionUsage astStateUsage) {
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    var succs = getSuccessions((ASTActionUsage) parent);
    return succs.stream().filter(t -> t.getSrc().equals(astStateUsage.getName())).map(
        t -> astStateUsage.getEnclosingScope().resolveActionUsage(t.getTgt()).get().getAstNode()).collect(
        Collectors.toList());
  }

  public boolean isMergeNode(ASTActionUsage actionUsage) {
    return actionUsage instanceof ASTMergeAction;
  }

  public List<List<ASTSysMLSuccession>> getDecisionPaths(ASTActionUsage decisionNode, ASTActionUsage mergeNode) {
    var parent = decisionNode.getEnclosingScope().getAstNode();
    var succs = getSuccessions((ASTActionUsage) parent);
    var succsFromDecision = succs.stream().filter(t -> t.getSrc().equals(decisionNode.getName())).collect(
        Collectors.toList());
    List<ASTSysMLSuccession> lastPath = getEndPath((ASTActionUsage) parent);
    var succssesorPaths = getDirectSuccessorList(decisionNode).stream().map(t -> getPathFromAction(t, succs)).collect(
        Collectors.toList());
    for (ASTSysMLSuccession succession : succsFromDecision) {
      var matched = succssesorPaths.stream().filter(t -> !t.isEmpty()).filter(
          t -> t.get(0).getSrc().equals(succession.getTgt())).collect(
          Collectors.toList());
      if(matched.size() > 0) {
        matched.forEach(t -> t.add(0, succession));
      }
      else {
        succssesorPaths.stream().filter(t -> t.isEmpty()).forEach(t -> t.add(0, succession));
      }
    }
    succssesorPaths.forEach(t -> t.addAll(lastPath));
    return succssesorPaths;
  }

  public List<ASTAttributeUsage> getParameters(ASTActionUsage actionUsage) {
    return actionUsage.streamSysMLElements().filter(t -> t instanceof ASTAttributeUsage).map(
        t -> (ASTAttributeUsage) t).filter(t -> t.isPresentSysMLFeatureDirection()).collect(
        Collectors.toList());
  }

  public List<ASTAttributeUsage> getFromAllSubActions(ASTActionUsage actionUsage) {
    var subActions = actionUsage.streamSysMLElements().filter(t -> t instanceof ASTActionUsage).map(
        t -> (ASTActionUsage) t).collect(Collectors.toList());
    return subActions.stream().flatMap(t -> getParameters(t).stream()).collect(Collectors.toList());
  }
  public ASTActionUsage resolveAction(String name, ASTSysMLSuccession succession){
    return succession.getEnclosingScope().resolveActionUsage(name).get().getAstNode();
  }
}
