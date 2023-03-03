package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionResolveUtils {

  static public List<ASTActionUsage> getActionsOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<List<ASTActionUsage>> parentActions;
    List<ASTActionUsage> attributeUsages = getActionUsageOfNode(node);

    parentActions = parentList.stream().map(ActionResolveUtils::getActionsOfElement).collect(Collectors.toList());
    attributeUsages.addAll(removeDuplicateActions(parentActions));
    return attributeUsages;
  }

  static List<ASTActionUsage> getActionUsageOfNode(ASTSysMLElement node) {
    List<ASTActionUsage> portUsageList = new ArrayList<>();
    if(node instanceof ASTPartDef) {
      portUsageList = ((ASTPartDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTActionUsage).map(f -> (ASTActionUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      portUsageList = ((ASTPartUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTActionUsage).map(f -> (ASTActionUsage) f).collect(
          Collectors.toList());
    }
    return portUsageList;
  }

  static List<ASTActionUsage> removeDuplicateActions(List<List<ASTActionUsage>> actionList) {

    Set<String> stringSet = actionList.stream().flatMap(Collection::stream).map(ASTActionUsage::getName).collect(
        Collectors.toSet());

    List<List<ASTActionUsage>> returnList = new ArrayList<>(actionList);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }
}
