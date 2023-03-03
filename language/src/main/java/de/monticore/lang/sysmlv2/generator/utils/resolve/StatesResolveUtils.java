package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTEntryAction;
import de.monticore.lang.sysmlstates._ast.ASTExitAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateDefTOP;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateUsageTOP;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatesResolveUtils {

  static public List<ASTStateUsage> getStatesOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<List<ASTStateUsage>> parentState;
    List<ASTStateUsage> stateUsages = getStateUsageOfNode(node);

    parentState = parentList.stream().map(StatesResolveUtils::getStatesOfElement).collect(Collectors.toList());
    stateUsages.addAll(removeDuplicateStates(parentState));
    return stateUsages;
  }

  static public List<ASTEntryAction> getEntryActionsOfElement(ASTStateUsage node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<ASTEntryAction> entryActionList = node.getEntryActionList();
    var entryActionsUsages = parentList.stream().filter(t -> t instanceof ASTStateDef).map(
        t -> (ASTStateDef) t).flatMap(ASTStateDefTOP::streamEntryActions).collect(Collectors.toList());
    var entryActionsDefs = parentList.stream().filter(t -> t instanceof ASTStateUsage).map(
        t -> (ASTStateUsage) t).flatMap(ASTStateUsageTOP::streamEntryActions).collect(Collectors.toList());
    entryActionList.addAll(entryActionsUsages);
    entryActionList.addAll(entryActionsDefs);
    return entryActionList;
  }

  static public List<ASTDoAction> getDoActionsOfElement(ASTStateUsage node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<ASTDoAction> entryActionList = node.getDoActionList();
    var entryActionsUsages = parentList.stream().filter(t -> t instanceof ASTStateDef).map(
        t -> (ASTStateDef) t).flatMap(ASTStateDefTOP::streamDoActions).collect(Collectors.toList());
    var entryActionsDefs = parentList.stream().filter(t -> t instanceof ASTStateUsage).map(
        t -> (ASTStateUsage) t).flatMap(ASTStateUsageTOP::streamDoActions).collect(Collectors.toList());
    entryActionList.addAll(entryActionsUsages);
    entryActionList.addAll(entryActionsDefs);
    return entryActionList;
  }

  static public List<ASTExitAction> getExitActionsOfElement(ASTStateUsage node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<ASTExitAction> entryActionList = node.getExitActionList();
    var entryActionsUsages = parentList.stream().filter(t -> t instanceof ASTStateDef).map(
        t -> (ASTStateDef) t).flatMap(ASTStateDefTOP::streamExitActions).collect(Collectors.toList());
    var entryActionsDefs = parentList.stream().filter(t -> t instanceof ASTStateUsage).map(
        t -> (ASTStateUsage) t).flatMap(ASTStateUsageTOP::streamExitActions).collect(Collectors.toList());
    entryActionList.addAll(entryActionsUsages);
    entryActionList.addAll(entryActionsDefs);
    return entryActionList;
  }

  static List<ASTStateUsage> getStateUsageOfNode(ASTSysMLElement node) {
    List<ASTStateUsage> stateUsageList = new ArrayList<>();
    if(node instanceof ASTStateDef) {
      stateUsageList = ((ASTStateDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTStateUsage).map(f -> (ASTStateUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTStateUsage) {
      stateUsageList = ((ASTStateUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTStateUsage).map(f -> (ASTStateUsage) f).collect(
          Collectors.toList());
    }
    return stateUsageList;
  }

  static List<ASTStateUsage> removeDuplicateStates(List<List<ASTStateUsage>> stateList) {

    Set<String> stringSet = stateList.stream().flatMap(Collection::stream).map(ASTStateUsage::getName).collect(
        Collectors.toSet());

    List<List<ASTStateUsage>> returnList = new ArrayList<>(stateList);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }

  static public List<ASTSysMLTransition> getTransitionOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<List<ASTSysMLTransition>> parentTransition;
    List<ASTSysMLTransition> transitions = getTransitionUsageOfNode(node);

    parentTransition = parentList.stream().map(StatesResolveUtils::getTransitionOfElement).collect(Collectors.toList());
    transitions.addAll(parentTransition.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    return transitions;
  }

  static List<ASTSysMLTransition> getTransitionUsageOfNode(ASTSysMLElement node) {
    List<ASTSysMLTransition> transitionList = new ArrayList<>();
    if(node instanceof ASTStateDef) {
      transitionList = ((ASTStateDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTSysMLTransition).map(f -> (ASTSysMLTransition) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTStateUsage) {
      transitionList = ((ASTStateUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTSysMLTransition).map(f -> (ASTSysMLTransition) f).collect(
          Collectors.toList());
    }
    return transitionList;
  }
}
