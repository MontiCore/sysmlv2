package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.*;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.*;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PortResolveUtils {
  ResolveUtils resolveUtils = new ResolveUtils();

  public List<ASTPortUsage> getPortsOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = resolveUtils.getDirectSupertypes(node);
    List<List<ASTPortUsage>> parentPort;
    List<ASTPortUsage> attributeUsages = getPortUsageOfNode(node);

    parentPort = parentList.stream().map(this::getPortsOfElement).collect(Collectors.toList());
    attributeUsages.addAll(removeDuplicateAttributes(parentPort));
    return attributeUsages;
  }

  List<ASTPortUsage> getPortUsageOfNode(ASTSysMLElement node) {
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

  List<ASTPortUsage> removeDuplicateAttributes(List<List<ASTPortUsage>> attributeLists) {

    Set<String> stringSet = attributeLists.stream().flatMap(Collection::stream).map(ASTPortUsage::getName).collect(
        Collectors.toSet());

    List<List<ASTPortUsage>> returnList = new ArrayList<>(attributeLists);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
