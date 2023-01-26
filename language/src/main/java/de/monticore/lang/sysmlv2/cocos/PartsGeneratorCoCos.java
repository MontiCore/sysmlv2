package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartsGeneratorCoCos implements SysMLPartsASTPartUsageCoCo, SysMLPartsASTPartDefCoCo {

  /**
   * Check that at least one part def is extended.
   */
  @Override public void check(ASTPartUsage node) {
    var specialications = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var redefinitons = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLRedefinition).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var typing = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLTyping).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var relevantElements = (int) node.getSysMLElementList().stream().filter(
        t -> t instanceof ASTActionDef | t instanceof ASTActionUsage | t instanceof ASTAttributeUsage
            | t instanceof ASTPartUsage).count(); //partUsage with at least one of the types is seen as a adhoc class definition
    if(specialications.isEmpty() && relevantElements == 0 && redefinitons.isEmpty() && typing.isEmpty()) {
      Log.error("The Part Usage " + node.getName()
          + " needs a type (at least one part def), redefine a part usage or specialize another part usage");
    }
    checkDisjunctAttributes(node);
  }
  @Override public void check(ASTPartDef node) {
    checkDisjunctAttributes(node);
  }
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
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

  List<ASTAttributeUsage> checkDisjunctAttributes(ASTSysMLElement node) {
    String name = null;
    List<ASTSysMLElement> parentList = new ArrayList<>();
    //Get direct supertypes
    if(node instanceof ASTPartDef) {
      name = ((ASTPartDef) node).getName();
      parentList = ((ASTPartDef) node).streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartDef) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      name = ((ASTPartUsage) node).getName();
      parentList = ((ASTPartUsage) node).streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartUsage(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTPartUsage) node).streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    List<List<ASTAttributeUsage>> parentAttribute = new ArrayList<>();
    List<ASTAttributeUsage> intersectList;
    //if no supertypes then the list of intersecting attributes is empty
    if(parentList.isEmpty()) {
      intersectList = new ArrayList<>();
    }
    else {

      parentAttribute = parentList.stream().map(this::checkDisjunctAttributes).collect(Collectors.toList());
      List<ASTAttributeUsage> intersectionOfList = emptyIntersection(parentAttribute);

      List<ASTAttributeUsage> attributeUsagesOfNode = getAttributeUsageOfNode(node);
      List<String> namesOfAttributesWithRedefinitions = attributeUsagesOfNode.stream().flatMap(
          ASTAttributeUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
          t -> t.getSuperTypesList().stream()).map(this::printName).collect(
          Collectors.toList());
      var listOfLists = new ArrayList<List<ASTAttributeUsage>>();
      listOfLists.add(intersectionOfList);

      intersectList = attributeUsageListUnion(listOfLists, namesOfAttributesWithRedefinitions);
    }

    if(intersectList.isEmpty()) {

      List<ASTAttributeUsage> attributeUsages = getAttributeUsageOfNode(node);
      List<String> listOfRedefinedAttributes = attributeUsages.stream().flatMap(
          ASTAttributeUsage::streamSpecializations).filter(t -> t instanceof ASTSysMLRedefinition).flatMap(
          t -> t.getSuperTypesList().stream()).map(t -> printName(t)).collect(
          Collectors.toList());
      attributeUsages.addAll(attributeUsageListUnion(parentAttribute, listOfRedefinedAttributes));
      return attributeUsages;
    }
    else {
      Log.error(
          "The supertypes of " + name + " contain a list of attribute usages that intersects, this is not allowed.");
    }
    return new ArrayList<>();
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
      var intersectingAttributeList = notEmpty.stream().flatMap(t -> t.stream()).filter(
          t -> stringIntersection.contains((t.getName()))).collect(
          Collectors.toList());
      return intersectingAttributeList;
    }
    else
      return new ArrayList<>();
  }

  List<ASTAttributeUsage> attributeUsageListUnion(List<List<ASTAttributeUsage>> attributeLists,
                                                  List<String> undesiredAttributes) {
    return attributeLists.stream().flatMap(Collection::stream).filter(
        t -> !(undesiredAttributes.contains(t.getName()))).collect(Collectors.toList());
  }
}
