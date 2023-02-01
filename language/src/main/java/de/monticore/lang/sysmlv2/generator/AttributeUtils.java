package de.monticore.lang.sysmlv2.generator;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttributeUtils {
  public List<ASTAttributeUsage> checkDisjunctAttributes(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = getDirectSupertypes(node);
    List<List<ASTAttributeUsage>> parentAttribute = new ArrayList<>();
    List<ASTAttributeUsage> intersectList;
    //if no supertypes then the list of intersecting attributes is empty
    if(parentList.isEmpty()) {
      intersectList = new ArrayList<>();
    }
    else {
      parentAttribute = parentList.stream().map(this::checkDisjunctAttributes).collect(Collectors.toList());
      List<ASTAttributeUsage> intersectionOfList = emptyIntersection(parentAttribute);

      intersectList = filterAttributeList(node, intersectionOfList);
    }

    if(intersectList.isEmpty()) {

      List<ASTAttributeUsage> attributeUsages = getAttributeUsageOfNode(node);
      List<String> listOfRedefinedAttributes = attributeUsages.stream().flatMap(
          ASTAttributeUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
          t -> t.getSuperTypesList().stream()).map(this::printName).collect(
          Collectors.toList());
      attributeUsages.addAll(attributeUsageListUnion(parentAttribute, listOfRedefinedAttributes));
      return attributeUsages;
    }
    else {
      Log.error(
          "The supertypes of " + getNameOfNode(node)
              + " contain a list of attribute usages that intersects, this is not allowed.");
    }
    return new ArrayList<>();
  }

  List<ASTAttributeUsage> filterAttributeList(ASTSysMLElement node, List<ASTAttributeUsage> listOfAttributeUsages) {

    List<ASTAttributeUsage> attributeUsagesOfNode = getAttributeUsageOfNode(node);
    List<String> namesOfAttributesWithRedefinitions = attributeUsagesOfNode.stream().flatMap(
        ASTAttributeUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
        t -> t.getSuperTypesList().stream()).map(this::printName).collect(
        Collectors.toList());

    for (ASTAttributeUsage element : listOfAttributeUsages) {
      var superTypesElement = getAttributeTypes(element).stream().map(this::printName).collect(Collectors.toList());
      var listOfSupertypeLists = listOfAttributeUsages.stream().filter(t -> t.getName().equals(element.getName())).map(
          this::getAttributeTypes).collect(Collectors.toList());
      var filteredSupertypes = listOfSupertypeLists.stream().flatMap(
          t -> t.stream().filter(item -> !superTypesElement.contains(printName(item)))).collect(
          Collectors.toList());
      if(filteredSupertypes.isEmpty() && listOfSupertypeLists.size() > 1)
        listOfAttributeUsages.remove(element);
    }
    var listOfLists = new ArrayList<List<ASTAttributeUsage>>();
    listOfLists.add(listOfAttributeUsages);
    return attributeUsageListUnion(listOfLists, namesOfAttributesWithRedefinitions);
  }

  List<ASTAttributeUsage> emptyIntersection(List<List<ASTAttributeUsage>> attributeLists) {
    //remove all sublists that are empty
    List<List<ASTAttributeUsage>> notEmpty = attributeLists.stream().filter(t -> !t.isEmpty()).collect(
        Collectors.toList());
    if(notEmpty.size() > 1) {
      var stringListOfLists = notEmpty.stream().map(t -> t.stream().map(ASTAttributeUsage::getName).collect(
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

  List<ASTAttributeUsage> attributeUsageListUnion(List<List<ASTAttributeUsage>> attributeLists,
                                                  List<String> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.contains(t.getName()))).collect(Collectors.toList());
  }

  String getNameOfNode(ASTSysMLElement node) {
    if(node instanceof ASTPartDef)
      return ((ASTPartDef) node).getName();
    if(node instanceof ASTPartUsage)
      return ((ASTPartUsage) node).getName();

    return "";
  }

  List<ASTSysMLElement> getDirectSupertypes(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = new ArrayList<>();
    //Get direct supertypes
    if(node instanceof ASTPartDef) {
      parentList = ((ASTPartDef) node).streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartDef) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      parentList = ((ASTPartUsage) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartUsage(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTPartUsage) node).streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    return parentList;
  }

  List<ASTAttributeUsage> getAttributeUsageOfNode(ASTSysMLElement node) {
    List<ASTAttributeUsage> attributeUsageList = new ArrayList<>();
    if(node instanceof ASTPartDef) {
      attributeUsageList = ((ASTPartDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTAttributeUsage).map(f -> (ASTAttributeUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      attributeUsageList = ((ASTPartUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTAttributeUsage).map(f -> (ASTAttributeUsage) f).collect(
          Collectors.toList());
    }
    return attributeUsageList;
  }

  List<ASTMCType> getAttributeTypes(ASTAttributeUsage astAttributeUsage) {
    return astAttributeUsage.streamSpecializations().filter(f -> f instanceof ASTSysMLTyping).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

}
