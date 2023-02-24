package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2.generator.utils.resolve.StatesResolveUtils;
import de.monticore.lang.sysmlv2.types.CommonExpressionsJavaPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.stream.Collectors;

public class AutomatonHelper {
  ComponentHelper componentHelper = new ComponentHelper();

  StatesResolveUtils statesResolveUtils = new StatesResolveUtils();

  public List<ASTSysMLTransition> getAllTransitionsWithGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(state).stream().filter(ASTSysMLTransition::isPresentGuard).collect(
        Collectors.toList());
  }

  public boolean hasTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    return getAllTransitionsFrom(state).stream().anyMatch(t -> !t.isPresentGuard());
  }

  public List<ASTSysMLTransition> getAllTransitionsFrom(ASTStateUsage state) {
    var parent = state.getEnclosingScope().getAstNode();
    return statesResolveUtils.getTransitionOfElement((ASTSysMLElement) parent).stream().filter(
        t -> t.getSrc().equals(state.getName())).collect(
        Collectors.toList());

  }

  public ASTSysMLTransition getFirstTransitionWithoutGuardFrom(ASTStateUsage automaton, ASTStateUsage state) {
    List<ASTSysMLTransition> transitionList = getAllTransitionsFrom(state).stream().filter(
        t -> !t.isPresentGuard()).collect(Collectors.toList());
    if(!transitionList.isEmpty()) {
      return transitionList.get(0);
    }
    return null;
  }

  public boolean hasSuperState(ASTStateUsage state) {

    var parent = state.getEnclosingScope().getAstNode();
    return parent instanceof ASTStateUsage || parent instanceof ASTStateDef;
  }

  public boolean isFinalState(ASTStateUsage state) {
    return state.getName().equals("done");
  }

  public boolean isInitialState(ASTStateUsage state) {
    return state.getName().equals("start");
  }

  public String printExpression(ASTExpression expr) {
    CommonExpressionsJavaPrinter prettyPrinter = new CommonExpressionsJavaPrinter(new IndentPrinter());
    return prettyPrinter.prettyprint(expr);
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

  public String getParametersOfActionAsString(List<ASTSysMLElement> attributeUsageList) {
    return attributeUsageList.stream().map(t -> typeWithNameOfElement(t)).collect(
        Collectors.joining(","));
  }

  String typeWithNameOfElement(ASTSysMLElement element) {
    if(element instanceof ASTAttributeUsage) {
      return componentHelper.getAttributeType((ASTAttributeUsage) element) + " "
          + ((ASTAttributeUsage) element).getName();
    }
    if(element instanceof ASTPartUsage) {
      return componentHelper.getPartType(((ASTPartUsage) element)) + " " + ((ASTPartUsage) element).getName();
    }
    return "";
  }

  String resolveStatePrefix(ASTStateUsage element, String prefix) {
    var parent = element.getEnclosingScope().getAstNode();
    var parentOfParent = parent.getEnclosingScope().getAstNode();

    if(parent instanceof ASTStateUsage && (parentOfParent instanceof ASTStateUsage
        || parentOfParent instanceof ASTStateDef)) {
      return resolveStatePrefix((ASTStateUsage) parent, prefix + ((ASTStateUsage) parent).getName() + "_");
    }
    return prefix;
  }

  public String resolveStateName(ASTStateUsage element) {
    return resolveStatePrefix(element, "") + element.getName();

  }

  public String resolveTransitionName(ASTStateUsage state, String transitionEnd) {
    var resolvedState = state.getSpannedScope().resolveStateUsage(transitionEnd);
    if(resolvedState.isPresent()) {
      return resolveStateName(resolvedState.get().getAstNode());
    }
    else
      return transitionEnd;
  }

  public List<ASTStateUsage> getSubStates(ASTStateUsage stateUsage) {
    return statesResolveUtils.getStatesOfElement(stateUsage);
  }

  public boolean isAutomaton(String stateName, ASTStateUsage stateUsage) {
    var resolvedState = stateUsage.getSpannedScope().resolveStateUsage(stateName);
    if(resolvedState.isPresent()) {
      return resolvedState.get().getAstNode().getIsAutomaton();
    }
    return false;
  }

  public String resolveCurrentStateName(ASTStateUsage stateUsage) {
    if(stateUsage.getIsAutomaton()) {
      return "currentState_" + resolveStateName(stateUsage);
    }
    return "currentState";
  }
  public ASTStateUsage resolveStateUsage(String targetName, ASTStateUsage stateUsage) {
    var resolvedState = stateUsage.getSpannedScope().resolveStateUsage(targetName);
    return resolvedState.map(StateUsageSymbol::getAstNode).orElse(null);
  }

  public String resolveEnumName(ASTStateUsage automaton) {
    return automaton.getName() + "Enum";
  }
}
