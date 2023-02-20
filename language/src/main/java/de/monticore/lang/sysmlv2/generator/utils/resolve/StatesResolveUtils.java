package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
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
  ResolveUtils resolveUtils = new ResolveUtils();

  public List<ASTStateUsage> getStatesOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = resolveUtils.getDirectSupertypes(node);
    List<List<ASTStateUsage>> parentState;
    List<ASTStateUsage> stateUsages = getStateUsageOfNode(node);

    parentState = parentList.stream().map(this::getStatesOfElement).collect(Collectors.toList());
    stateUsages.addAll(removeDuplicateStates(parentState));
    return stateUsages;
  }

  List<ASTStateUsage> getStateUsageOfNode(ASTSysMLElement node) {
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

  List<ASTStateUsage> removeDuplicateStates(List<List<ASTStateUsage>> stateList) {

    Set<String> stringSet = stateList.stream().flatMap(Collection::stream).map(ASTStateUsage::getName).collect(
        Collectors.toSet());

    List<List<ASTStateUsage>> returnList = new ArrayList<>(stateList);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }

  public List<ASTSysMLTransition> getTransitionOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = resolveUtils.getDirectSupertypes(node);
    List<List<ASTSysMLTransition>> parentTransition;
    List<ASTSysMLTransition> transitions = getTransitionUsageOfNode(node);

    parentTransition = parentList.stream().map(this::getTransitionOfElement).collect(Collectors.toList());
    transitions.addAll(parentTransition.stream().flatMap(t -> t.stream()).collect(Collectors.toList()));
    return transitions;
  }

  List<ASTSysMLTransition> getTransitionUsageOfNode(ASTSysMLElement node) {
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

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
