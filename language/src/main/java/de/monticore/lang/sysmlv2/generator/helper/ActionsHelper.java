package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
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
        if(actionResolve.get().getAstNode() instanceof ASTSendActionUsage)
          return true;
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
    var succList = actionUsage.streamSysMLElements().filter(t -> t instanceof ASTSysMLSuccession).map(
        t -> (ASTSysMLSuccession) t).collect(
        Collectors.toList());

    List<ASTSysMLSuccession> orderedList = new ArrayList<>();
    if(!succList.isEmpty()) {
      var successionOptionalFirst = succList.stream().filter(t -> t.getSrc().equals("start")).findFirst();

      if(successionOptionalFirst.isPresent()) {
        ASTSysMLSuccession currentSuccession = successionOptionalFirst.get();
        orderedList.add(currentSuccession);
        for (int index = 1; index < succList.size(); index++) {

          ASTSysMLSuccession finalCurrentSuccession = currentSuccession;
          var successionOptional = succList.stream().filter(
              t -> t.getSrc().equals(finalCurrentSuccession.getTgt())).findFirst();
          if(successionOptional.isPresent()) {
            currentSuccession = succList.stream().filter(
                t -> t.getSrc().equals(finalCurrentSuccession.getTgt())).findFirst().get();
            orderedList.add(currentSuccession);
          }
        }
      }
    }
    return orderedList;
  }
}
