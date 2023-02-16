package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlv2.types.CommonExpressionsJavaPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.stream.Collectors;

public class AutomatonHelper {
  public List<ASTSysMLTransition> getAllTransitionsWithGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(automaton, state).stream().filter(ASTSysMLTransition::isPresentGuard).collect(
        Collectors.toList());
  }

  public boolean hasTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(automaton, state).stream().anyMatch(t -> !t.isPresentGuard());
  }

  public List<ASTSysMLTransition> getAllTransitionsFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return automaton.streamSysMLElements().filter(t -> t instanceof ASTSysMLTransition).map(
        t -> (ASTSysMLTransition) t).filter(t -> t.getSrc().equals(state.getName())).collect(
        Collectors.toList());
  }

  public ASTSysMLTransition getFirstTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    List<ASTSysMLTransition> transitionList = getAllTransitionsFrom(automaton, state).stream().filter(
        t -> !t.isPresentGuard()).collect(Collectors.toList());
    if(!transitionList.isEmpty()) {
      return transitionList.get(0);
    }
    return null;
  }

  public boolean hasSuperState(ASTStateUsage automaton, ASTStateUsage state) {
    //TODO

    return false;
  }

  public boolean isFinalState(ASTStateUsage automaton, ASTStateUsage state) {
    return false;
  }

  public boolean isInitialState(ASTStateUsage automaton, ASTStateUsage state) {
    return false;
  }

  public String printExpression(ASTExpression expr) {
    CommonExpressionsJavaPrinter prettyPrinter = new CommonExpressionsJavaPrinter(new IndentPrinter());
    return prettyPrinter.prettyprint(expr);
  }

  public boolean isSendAction(ASTDoAction doAction) {
    if(doAction.isPresentActionUsage()) {
      return doAction.getActionUsage() instanceof ASTSendActionUsage;
    }
    else {
      //TODO resolve name of action and check if its a send action
    }
    return false;
  }

  public ASTSendActionUsage castToSend(ASTDoAction doAction) {
    if(doAction.isPresentActionUsage()) {
      return (ASTSendActionUsage) doAction.getActionUsage();
    }
    else {
      //TODO
      return null;
    }
  }

  public String getNameOfDoAction(ASTDoAction doAction) {
    return doAction.getAction();
  }

  public boolean hasEntryAction(ASTStateUsage stateUsage) {
    return stateUsage.getEntryActionList().size() > 0;
  }

  public boolean hasExitAction(ASTStateUsage stateUsage) {
    return stateUsage.getExitActionList().size() > 0;
  }

  public boolean hasDoAction(ASTStateUsage stateUsage) {
    return stateUsage.getDoActionList().size() > 0;
  }
}
