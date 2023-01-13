package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

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
    for (ASTSpecialization redefinition : redefinitionList) {
      if(redefinition.getSuperTypesList().size() != 1) {
        Log.error("Attribute Usage " + node.getName() + " has "
            + redefinition.getSuperTypesList().size()
            + " redefinitions, but may only redefine 1.");
      }
      String parentAttributeName = printName(redefinition.getSuperTypes(0));

      if(!node.getName().equals(parentAttributeName)) {
        Log.error("Attribute Usage " + node.getName()
            + " has to redefine an Attribute Usage with the same name, but redefines "
            + parentAttributeName + ".");
      }
      checkAttributeDefined(node, parentAttributeName);
    }

  }

  void checkTypingExists(ASTAttributeUsage node) {
    long typeCount = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLTyping).count();
    if(typeCount != 1) {
      Log.error("Attribute usage " + node.getName() + " has " + typeCount + " typings, but needs exactly 1.");
    }
  }

  void checkAttributeDefined(ASTAttributeUsage attributeUsage, String redefinedAttributeName) {
    int attributeFoundInParents = 0; // we count how often the redefinedAttribute is defined within the transitiveSupertypes of the parent node
    var parentNode = attributeUsage.getEnclosingScope().getAstNode();

    if(parentNode instanceof ASTPartDef) {
      for (ASTPartDef transSuperType : ((ASTPartDef) parentNode).getTransitiveDefSupertypes()) {
        var attribute = transSuperType.getSpannedScope().resolveAttributeUsage(redefinedAttributeName);
        if(attribute.isPresent()) {
          ASTAttributeUsage refinedAttr = attribute.get().getAstNode();
          if(refinedAttr.getSpecializationList().stream().filter(
              t -> t instanceof ASTSysMLRedefinition).count() == 0) //we only count attributeUsages where no redefinition is used
          {
            attributeFoundInParents++;
          }

          checkTypeCompatibility(attributeUsage, refinedAttr);

        }
      }
      if(attributeFoundInParents != 1)
        Log.error("Attribute usage " + attributeUsage.getName() + " was found " + attributeFoundInParents
            + " in the transitive super types of its parent, but only 1 is allowed.");

    }
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
