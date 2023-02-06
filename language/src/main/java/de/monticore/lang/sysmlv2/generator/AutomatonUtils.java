package de.monticore.lang.sysmlv2.generator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.stream.Collectors;

public class AutomatonUtils {
  public List<ASTSysMLSuccession> getAllTransitionsWithGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(automaton, state).stream().filter(t -> t.isPresentGuard()).collect(
        Collectors.toList());
  }

  public boolean hasTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(automaton, state).stream().anyMatch(t -> !t.isPresentGuard());
  }

  public List<ASTSysMLSuccession> getAllTransitionsFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return automaton.streamSysMLElements().filter(t -> t instanceof ASTSysMLSuccession).map(
        t -> (ASTSysMLSuccession) t).filter(t -> t.getSrc().equals(state.getName())).collect(
        Collectors.toList());
  }

  public ASTSysMLSuccession getFirstTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    List<ASTSysMLSuccession> successionList = getAllTransitionsFrom(automaton, state).stream().filter(
        t -> !t.isPresentGuard()).collect(Collectors.toList());
    if(!successionList.isEmpty()) {
      return successionList.get(0);
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

    ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
    return prettyPrinter.prettyprint(expr);
  }

  public boolean isPresentGuard(ASTSysMLSuccession transition) {
    return transition.isPresentGuard();
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
