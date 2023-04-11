package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.*;
import de.monticore.lang.sysmlparts._ast.*;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PortResolveUtils {

  static public List<ASTPortUsage> getPortsOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<List<ASTPortUsage>> parentPort;
    List<ASTPortUsage> attributeUsages = getPortUsageOfNode(node);

    parentPort = parentList.stream().map(PortResolveUtils::getPortsOfElement).collect(Collectors.toList());
    attributeUsages.addAll(removeDuplicateAttributes(parentPort));
    return attributeUsages;
  }

  static List<ASTPortUsage> getPortUsageOfNode(ASTSysMLElement node) {
    List<ASTPortUsage> portUsageList = new ArrayList<>();
    if(node instanceof ASTPartDef) {
      portUsageList = ((ASTPartDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPortUsage).map(f -> (ASTPortUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      portUsageList = ((ASTPartUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPortUsage).map(f -> (ASTPortUsage) f).collect(
          Collectors.toList());
    }
    return portUsageList;
  }

  static List<ASTPortUsage> removeDuplicateAttributes(List<List<ASTPortUsage>> attributeLists) {

    Set<String> stringSet = attributeLists.stream().flatMap(Collection::stream).map(ASTPortUsage::getName).collect(
        Collectors.toSet());

    List<List<ASTPortUsage>> returnList = new ArrayList<>(attributeLists);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }

  static public Optional<PortUsageSymbol> resolvePort(String QName, String BaseName, ISysMLPartsScope scope) {
    String parentPart;
    Optional<PartDefSymbol> parentPartDef = Optional.empty();
    Optional<PartUsageSymbol> parentPartUsage = Optional.empty();
    if(scope.getAstNode() instanceof ASTPartDef){
      parentPartDef = Optional.ofNullable(((ASTPartDef) scope.getAstNode()).getSymbol());
    }
    if(scope.getAstNode() instanceof ASTPartUsage){
      parentPartUsage = Optional.ofNullable(((ASTPartUsage) scope.getAstNode()).getSymbol());
    }
    if(!QName.equals(BaseName)) {
      if(QName.split("\\.").length > 2) {
        var symbol = PartResolveUtils.resolvePartQname(QName.substring(0, QName.length() - BaseName.length() - 1),
            scope);
        if(symbol.isPresent()) {
          if(symbol.get() instanceof PartUsageSymbol)
            parentPartUsage = Optional.of((PartUsageSymbol) symbol.get());
        }
      }
      else {
        parentPart = QName.substring(0, QName.length() - BaseName.length() - 1);
        parentPartDef = scope.resolvePartDefDown(parentPart);
        parentPartUsage = scope.resolvePartUsageDown(parentPart);
      }
    }

    //check first if its present in transitive supertypes of parts
    if(parentPartUsage.isPresent()) {
      var portUsageList = getPortsOfElement(parentPartUsage.get().getAstNode());
      if(portUsageList.stream().anyMatch(t -> t.getName().equals(BaseName)))
        return portUsageList.stream().filter(t -> t.getName().equals(BaseName)).map(ASTPortUsageTOP::getSymbol).findFirst();
    }
    if(parentPartDef.isPresent()) {
      var portUsageList = getPortsOfElement(parentPartDef.get().getAstNode());
      if(portUsageList.stream().anyMatch(t -> t.getName().equals(BaseName)))
        return portUsageList.stream().filter(t -> t.getName().equals(BaseName)).map(ASTPortUsageTOP::getSymbol).findFirst();
    }

    return scope.resolvePortUsageDown(QName);

  }
}
