package de.monticore.lang.sysmlv2.generator;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
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

    List<ASTAttributeUsage> attributeUsages = getAttributeUsageOfNode(node);
    List<ASTAttributeUsage> listOfRedefinedAttributes = attributeUsages.stream().filter(
        t -> t.streamSpecializations().filter(f -> f instanceof ASTSysMLRedefinition).count()>0).collect(Collectors.toList());

    if(parentList.isEmpty()) {
      intersectList = new ArrayList<>();
      if(listOfRedefinedAttributes.size() != 0) {
        Log.error(
            "The supertypes of " + getNameOfNode(node)
                + " are empty, but " + getNameOfNode(node)
                + " has an attribute usage which uses redefinition, this is not allowed.");
      }
    }
    else {
      parentAttribute = parentList.stream().map(this::checkDisjunctAttributes).collect(Collectors.toList());
      intersectList = emptyIntersection(parentAttribute);
      checkRedefinitionTypes(listOfRedefinedAttributes, parentAttribute);
    }

    intersectList = filterSameTypeInAttributeList(node, intersectList);
    if(intersectList.isEmpty()) {

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

  void checkRedefinitionTypes(List<ASTAttributeUsage> redefinedTypes,
                              List<List<ASTAttributeUsage>> parentAttributeList) {

    for (ASTAttributeUsage attributeUsage : redefinedTypes) {
      String attributeName = attributeUsage.getName();
      List<ASTMCType> redefinedASTMCTypes = attributeUsage.streamSpecializations().filter(
          t -> t instanceof ASTSysMLTyping).flatMap(t -> t.streamSuperTypes()).collect(
          Collectors.toList());

      var astmcTypesFromParents = parentAttributeList.stream().flatMap(t -> t.stream()).filter(
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

  boolean checkCompatibility(ASTMCType first, ASTMCType second, ISysMLPartsScope parts) {
    if(printName(first).equals(printName(second)))
      return true;
    var optionalAttributeDef = parts.resolveAttributeDef(printName(first));
    if(optionalAttributeDef.isPresent()) {
      var attributeDef = optionalAttributeDef.get().getAstNode();
      var superTypesAttributeDef = attributeDef.streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(t -> t.streamSuperTypes()).collect(
          Collectors.toList());
      var dsad = superTypesAttributeDef.stream().map(
          t -> checkCompatibility(t, second, attributeDef.getEnclosingScope())).collect(Collectors.toList());
      return dsad.size() != 0;

    }

    else
      return false;
  }

  List<ASTAttributeUsage> filterSameTypeInAttributeList(ASTSysMLElement node,
                                                        List<ASTAttributeUsage> listOfAttributeUsages) {

    List<ASTAttributeUsage> attributeUsagesOfNode = getAttributeUsageOfNode(node);
    List<ASTMCType> namesOfAttributesWithRedefinitions = attributeUsagesOfNode.stream().flatMap(
        ASTAttributeUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
        t -> t.getSuperTypesList().stream()).collect(
        Collectors.toList());
    List<ASTAttributeUsage> returnList = new ArrayList<>(listOfAttributeUsages);
    for (ASTAttributeUsage element : listOfAttributeUsages) {
      var superTypesElement = getAttributeTypes(element).stream().map(this::printName).collect(Collectors.toList());
      var listOfSupertypeLists = listOfAttributeUsages.stream().filter(t -> t.getName().equals(element.getName())).map(
          this::getAttributeTypes).collect(Collectors.toList());
      var filteredSupertypes = listOfSupertypeLists.stream().flatMap(
          t -> t.stream().filter(item -> !superTypesElement.contains(printName(item)))).collect(
          Collectors.toList());
      if(filteredSupertypes.isEmpty() && listOfSupertypeLists.size() > 1)
        returnList.removeIf(t -> t.getName().equals(element.getName()));
    }
    var listOfLists = new ArrayList<List<ASTAttributeUsage>>();
    listOfLists.add(returnList);
    return astMcTypeListUnion(listOfLists, namesOfAttributesWithRedefinitions);
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
                                                  List<ASTAttributeUsage> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.stream().map(ASTAttributeUsage::getName).collect(Collectors.toList()).contains(
            t.getName()))).collect(Collectors.toList());
  }
  List<ASTAttributeUsage> astMcTypeListUnion(List<List<ASTAttributeUsage>> attributeLists,
                                                  List<ASTMCType> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.stream().map(this::printName).collect(Collectors.toList()).contains(
            t.getName()))).collect(Collectors.toList());
  }

  String getNameOfNode(ASTSysMLElement node) {
    if(node instanceof ASTPartDef)
      return ((ASTPartDef) node).getName();
    if(node instanceof ASTPartUsage)
      return ((ASTPartUsage) node).getName();
    if(node instanceof ASTAttributeDef)
      return ((ASTAttributeDef) node).getName();
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
    if(node instanceof ASTAttributeDef) {
      parentList = ((ASTAttributeDef) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTAttributeDef) node).getEnclosingScope().resolveAttributeDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
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
    if(node instanceof ASTAttributeDef) {
      attributeUsageList = ((ASTAttributeDef) node).getSysMLElementList().stream().filter(
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
