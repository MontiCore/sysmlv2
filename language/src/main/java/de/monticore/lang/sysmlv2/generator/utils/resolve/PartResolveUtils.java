package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsageTOP;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.ICommonSysMLPartsSymbol;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartResolveUtils {

  public static List<ASTPartUsage> getSubPartsOfElement(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = ResolveUtils.getDirectSupertypes(node);
    List<List<ASTPartUsage>> parentPart = new ArrayList<>();
    List<ASTPartUsage> intersectList;
    //if no supertypes then the list of intersecting attributes is empty

    List<ASTPartUsage> partUsages = getPartUsageOfNode(node);
    List<ASTPartUsage> listOfRedefinedParts = partUsages.stream().filter(
        t -> t.streamSpecializations().anyMatch(f -> f instanceof ASTSysMLRedefinition)).collect(
        Collectors.toList());

    if(parentList.isEmpty()) {
      intersectList = new ArrayList<>();
      if(listOfRedefinedParts.size() != 0) {
        Log.error(
            "The supertypes of " + getNameOfNode(node)
                + " are empty, but " + getNameOfNode(node)
                + " has an attribute usage which uses redefinition, this is not allowed.");
      }
    }
    else {
      parentPart = parentList.stream().map(PartResolveUtils::getSubPartsOfElement).collect(Collectors.toList());
      intersectList = emptyIntersection(parentPart);
      checkRedefinitionTypes(listOfRedefinedParts, parentPart);
    }

    intersectList = filterSameTypeInAttributeList(node, intersectList);
    if(intersectList.isEmpty()) {

      List<ASTPartUsage> parentWithoutRedefinedParts = attributeUsageListUnion(parentPart, listOfRedefinedParts);
      List<ASTPartUsage> partsWithoutRedefinition = new ArrayList<>(partUsages);
      partsWithoutRedefinition.removeAll(listOfRedefinedParts);
      List<String> stringList = partsWithoutRedefinition.stream().map(ASTPartUsageTOP::getName).collect(
          Collectors.toList());
      if(parentWithoutRedefinedParts.stream().anyMatch(t -> stringList.contains(t.getName()))) {
        Log.error(
            "A subparts of " + getNameOfNode(node)
                + " has the same name of a transitive subpart without redefining it.");
      }
      partUsages.addAll(parentWithoutRedefinedParts);
      return partUsages;
    }
    else {
      Log.error(
          "The supertypes of " + getNameOfNode(node)
              + " contain a list of attribute usages that intersects, this is not allowed.");
    }
    return new ArrayList<>();
  }

  static void checkRedefinitionTypes(List<ASTPartUsage> redefinedTypes,
                                     List<List<ASTPartUsage>> parentAttributeList) {

    for (ASTPartUsage attributeUsage : redefinedTypes) {
      String attributeName = attributeUsage.getName();
      List<ASTMCType> redefinedASTMCTypes = attributeUsage.streamSpecializations().filter(
          t -> t instanceof ASTSysMLTyping).flatMap(ASTSpecialization::streamSuperTypes).collect(
          Collectors.toList());

      var astmcTypesFromParents = parentAttributeList.stream().flatMap(Collection::stream).filter(
          t -> t.getName().equals(attributeName)).flatMap(f -> getAttributeTypes(f).stream()).collect(
          Collectors.toList());

      for (ASTMCType type : redefinedASTMCTypes) {
        boolean found = astmcTypesFromParents.stream().filter(
            t -> checkCompatibility(type, t, attributeUsage.getEnclosingScope())).count()
            == astmcTypesFromParents.size();
        if(!found) {
          Log.error(
              "The Attribute Usage " + attributeName
                  + " has the type " + printName(type) + " ,that is not resolvable.");
        }

      }

    }
  }

  static boolean checkCompatibility(ASTMCType first, ASTMCType second, ISysMLPartsScope parts) {
    if(printName(first).equals(printName(second)))
      return true;
    //TODO
    var optionalAttributeDef = parts.resolveAttributeDef(printName(first));
    if(optionalAttributeDef.isPresent()) {
      var attributeDef = optionalAttributeDef.get().getAstNode();
      var superTypesAttributeDef = attributeDef.streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(ASTSpecialization::streamSuperTypes).collect(
          Collectors.toList());
      var dsad = superTypesAttributeDef.stream().map(
          t -> checkCompatibility(t, second, attributeDef.getEnclosingScope())).collect(Collectors.toList());
      return dsad.size() != 0;

    }

    else
      return false;
  }

  static List<ASTPartUsage> filterSameTypeInAttributeList(ASTSysMLElement node,
                                                          List<ASTPartUsage> listOfAttributeUsages) {

    List<ASTPartUsage> attributeUsagesOfNode = getPartUsageOfNode(node);
    List<ASTMCType> namesOfAttributesWithRedefinitions = attributeUsagesOfNode.stream().flatMap(
        ASTPartUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
        t -> t.getSuperTypesList().stream()).collect(
        Collectors.toList());
    List<ASTPartUsage> returnList = new ArrayList<>(listOfAttributeUsages);
    for (ASTPartUsage element : listOfAttributeUsages) {
      var superTypesElement = getAttributeTypes(element).stream().map(PartResolveUtils::printName).collect(
          Collectors.toList());
      var listOfSupertypeLists = listOfAttributeUsages.stream().filter(t -> t.getName().equals(element.getName())).map(
          PartResolveUtils::getAttributeTypes).collect(Collectors.toList());
      var filteredSupertypes = listOfSupertypeLists.stream().flatMap(
          t -> t.stream().filter(item -> !superTypesElement.contains(printName(item)))).collect(
          Collectors.toList());
      if(filteredSupertypes.isEmpty() && listOfSupertypeLists.size() > 1)
        returnList.removeIf(t -> t.getName().equals(element.getName()));
    }
    var listOfLists = new ArrayList<List<ASTPartUsage>>();
    listOfLists.add(returnList);
    return astMcTypeListUnion(listOfLists, namesOfAttributesWithRedefinitions);
  }

  static List<ASTPartUsage> emptyIntersection(List<List<ASTPartUsage>> attributeLists) {
    //remove all sublists that are empty
    List<List<ASTPartUsage>> notEmpty = attributeLists.stream().filter(t -> !t.isEmpty()).collect(
        Collectors.toList());
    if(notEmpty.size() > 1) {
      var stringListOfLists = notEmpty.stream().map(t -> t.stream().map(ASTPartUsage::getName).collect(
          Collectors.toList())).filter(t -> !t.isEmpty()).collect(Collectors.toList());
      List<String> stringIntersection = stringListOfLists.get(0);
      for (int i = 1; i < stringListOfLists.size(); i++) {
        stringIntersection.retainAll(stringListOfLists.get(i));
      }
      return notEmpty.stream().flatMap(Collection::stream).filter(
          t -> stringIntersection.contains((t.getName()))).collect(
          Collectors.toList());
    }
    else
      return new ArrayList<>();
  }

  static List<ASTPartUsage> attributeUsageListUnion(List<List<ASTPartUsage>> attributeLists,
                                                    List<ASTPartUsage> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.stream().map(ASTPartUsage::getName).collect(Collectors.toList()).contains(
            t.getName()))).collect(Collectors.toList());
  }

  static List<ASTPartUsage> astMcTypeListUnion(List<List<ASTPartUsage>> attributeLists,
                                               List<ASTMCType> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.stream().map(PartResolveUtils::printName).collect(Collectors.toList()).contains(
            t.getName()))).collect(Collectors.toList());
  }

  static String getNameOfNode(ASTSysMLElement node) {
    if(node instanceof ASTPartDef)
      return ((ASTPartDef) node).getName();
    if(node instanceof ASTPartUsage)
      return ((ASTPartUsage) node).getName();
    if(node instanceof ASTAttributeDef)
      return ((ASTAttributeDef) node).getName();
    if(node instanceof ASTPortDef)
      return ((ASTPortDef) node).getName();
    return "";
  }

  static public List<ASTPartUsage> getPartUsageOfNode(ASTSysMLElement node) {
    List<ASTPartUsage> attributeUsageList = new ArrayList<>();
    if(node instanceof ASTPartDef) {
      attributeUsageList = ((ASTPartDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      attributeUsageList = ((ASTPartUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTAttributeDef) {
      attributeUsageList = ((ASTAttributeDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortDef) {
      attributeUsageList = ((ASTPortDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortUsage) {
      attributeUsageList = ((ASTPortUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTSysMLPackage) {
      attributeUsageList = ((ASTSysMLPackage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPartUsage).map(f -> (ASTPartUsage) f).collect(
          Collectors.toList());
    }
    return attributeUsageList;
  }

  static List<ASTMCType> getAttributeTypes(ASTPartUsage astAttributeUsage) {

    //TODO hier muss gefixed werden
    return astAttributeUsage.streamSpecializations().filter(f -> f instanceof ASTSysMLTyping).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
  }

  static private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  static public Optional<ICommonSysMLPartsSymbol> resolvePartQname(String QName, ISysMLPartsScope scope) {
    var QnameArray = QName.split("\\.");
    var first = QnameArray[0];
    var resolvePartDef = scope.resolvePartDef(first);
    var resolvePartUsage = scope.resolvePartUsage(first);
    var newQName = String.join(".", Arrays.copyOfRange(QnameArray, 1, QnameArray.length));
    if(resolvePartDef.isPresent()) {
      var partDef = resolvePartDef.get().getAstNode();
      return resolvePartQnameWithParent(newQName,
          partDef);
    }
    if(resolvePartUsage.isPresent()) {
      var partUsage = resolvePartUsage.get().getAstNode();
      return resolvePartQnameWithParent(newQName,
          partUsage);
    }
    return Optional.empty();
  }

  static public Optional<ICommonSysMLPartsSymbol> resolvePartQnameWithParent(String QName, ASTSysMLElement element) {
    var QnameArray = QName.split("\\.");
    var first = QnameArray[0];
    var partUsageList = getSubPartsOfElement(element);
    if(partUsageList.stream().anyMatch(t -> t.getName().equals(first))) {
      var matchingParts = partUsageList.stream().filter(t -> t.getName().equals(first)).collect(Collectors.toList());
      if(matchingParts.size() > 1) {
        Log.error("Found more than one part named " + first + " in part usage " + getNameOfNode(element)
            + " this is not resolvable.");
      }
      if(QnameArray.length == 1)
        return Optional.ofNullable(matchingParts.get(0).getSymbol());
      var newQName = String.join(".", Arrays.copyOfRange(QnameArray, 1, QnameArray.length));
      return resolvePartQnameWithParent(newQName, matchingParts.get(0));
    }
    else {
      Log.error("Part named " + first + " in part usage " + getNameOfNode(element) + " is not resolvable.");
    }
    return Optional.empty();
  }

}
