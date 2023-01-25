package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttributeGeneratorCoCos implements SysMLPartsASTAttributeUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that only one redefinition is used and that it is resolvable.
   */
  @Override
  public void check(ASTAttributeUsage node) {
    checkTypingExists(node);
    var redefinitionList = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLRedefinition).collect(
        Collectors.toList());
    List<ASTAttributeUsage> attributeUsageList = checkAttributeDefinedInParents(node);
    long redefinitionCount = attributeUsageList.stream().flatMap(t -> t.getSpecializationList().stream()).filter(
        f -> f instanceof ASTSysMLRedefinition).count(); //counts the number of redefinitions
    //TODO CHECKPARENTS Optional<AttributeUsage raus> und dann check ich ob das mit redefinitions und type passt
    if(redefinitionList.isEmpty()) {
      if(attributeUsageList.size() != 0)
        Log.error("Attribute Usage " + node.getName()
            + " is already defined in the transitive supertypes of the parent element. Attribute Usages with the same name where found "
            + attributeUsageList.size() + " times.");
    }
    else {
      for (ASTSpecialization redefinition : redefinitionList) {
        if(redefinition.getSuperTypesList().size() != 1) {
          Log.error("Attribute Usage " + node.getName() + " has "
              + redefinition.getSuperTypesList().size()
              + " redefinitions, but may only redefine 1.");
        }
        if(attributeUsageList.size() != (1 + redefinitionCount)) {
          Log.error("Attribute Usage " + node.getName()
              + " has to be defined exactly once in the transitive supertypes of the parent element. Attribute Usages with the same name where found "
              + attributeUsageList.size() + " times.");
        }
        String parentAttributeName = printName(redefinition.getSuperTypes(0));

        if(!node.getName().equals(parentAttributeName)) {
          Log.error("Attribute Usage " + node.getName()
              + " has to redefine an Attribute Usage with the same name, but redefines "
              + parentAttributeName + ".");
        }
        checkTypeCompatibility(node, attributeUsageList.get(0));
      }
    }

  }

  void checkTypingExists(ASTAttributeUsage node) {
    long typeCount = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLTyping).count();
    if(typeCount != 1) {
      Log.error("Attribute usage " + node.getName() + " has " + typeCount + " typings, but needs exactly 1.");
    }
  }

  List<ASTAttributeUsage> checkAttributeDefinedInParents(ASTAttributeUsage attributeUsage) {
    List<ASTAttributeUsage> attributeUsageList = new ArrayList<>();
    var parentNode = attributeUsage.getEnclosingScope().getAstNode();
    String usageName = attributeUsage.getName();
    Optional<AttributeUsageSymbol> astAttributeUsage;
    if(parentNode instanceof ASTPartDef) {
      for (ASTPartDef transSuperType : ((ASTPartDef) parentNode).getTransitiveDefSupertypes()) {
        astAttributeUsage = transSuperType.getSpannedScope().resolveAttributeUsage(usageName);
        astAttributeUsage.ifPresent(attributeUsageSymbol -> attributeUsageList.add(attributeUsageSymbol.getAstNode()));
      }
    }
    if(parentNode instanceof ASTPartUsage) {
      for (ASTPartDef transSuperType : ((ASTPartUsage) parentNode).getTransitiveDefSupertypes()) {
        astAttributeUsage = transSuperType.getSpannedScope().resolveAttributeUsage(usageName);
        astAttributeUsage.ifPresent(attributeUsageSymbol -> attributeUsageList.add(attributeUsageSymbol.getAstNode()));
      }
      for (ASTPartUsage transSuperType : ((ASTPartUsage) parentNode).getTransitiveUsageSupertypes()) {
        astAttributeUsage = transSuperType.getSpannedScope().resolveAttributeUsage(usageName);
        astAttributeUsage.ifPresent(attributeUsageSymbol -> attributeUsageList.add(attributeUsageSymbol.getAstNode()));
      }
    }
    return attributeUsageList;
  }

  private void checkTypeCompatibility(ASTAttributeUsage attributeUsage, ASTAttributeUsage redefinedAttribute) {
    var attributeUsageType = attributeUsage.getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).collect(
        Collectors.toList()).get(0).getSuperTypes(0);
    var refinedUsageType = redefinedAttribute.getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).collect(
        Collectors.toList()).get(0).getSuperTypes(0);
    var attributeType = attributeUsage.getEnclosingScope().resolveAttributeDef(
        printName(attributeUsageType));

    var refinedType = redefinedAttribute.getEnclosingScope().resolveAttributeDef(
        printName(refinedUsageType));

    if(attributeType.isEmpty()) {
      Log.error(
          "The type " + printName(attributeUsageType) + " of the Attribute usage " + attributeUsage.getName()
              + " was not resolvable.");
    }
    if(refinedType.isEmpty()) {
      Log.error(
          "The type " + printName(refinedUsageType) + " of the Attribute usage " + redefinedAttribute.getName()
              + " was not resolvable.");
    }
    var specializationStringList = attributeType.get().getAstNode()
        .streamSpecializations()
        .filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(s -> s.streamSuperTypes())
        .map(s -> printName(s))
        .collect(Collectors.toList());
    if(!specializationStringList.contains(printName(refinedUsageType))) {
      Log.error(
          "The type " + printName(attributeUsageType) + " is not a a subtype of " + printName(refinedUsageType) + " .");

    }
  }

}
