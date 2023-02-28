package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class PortsGeneratorCoCos implements SysMLPartsASTPortUsageCoCo, SysMLPartsASTPortDefCoCo {
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that at least one part def is extended.
   */
  @Override public void check(ASTPortUsage node) {

    if(node.isPresentSysMLFeatureDirection()) {
      Log.error("The Port usage " + node.getName() + " has a direction, but is not allowed to have one.");
    }
    if(node.streamSpecializations().anyMatch(t -> !(t instanceof ASTSysMLTyping))) {
      Log.error("The Port usage " + node.getName() + " has specialications that are not typings, this is not allowed.");
    }
    if(node.streamSpecializations().filter(t -> (t instanceof ASTSysMLTyping)).count() > 1) {
      Log.error("The Port usage " + node.getName() + " has more than one type this is not allowed.");
    }
    if(node.streamSysMLElements().anyMatch(t -> !(t instanceof ASTAttributeUsage))) {
      Log.error(
          "The Port usage " + node.getName() + " has sub elements that are not attribute usages this is not allowed.");
    }
    AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();
    var attributeUsageList = attributeResolveUtils.getAttributesOfElement(node);
    checkAttributes(node.getName(), attributeUsageList);
  }

  @Override public void check(ASTPortDef node) {

    if(node.streamSysMLElements().anyMatch(t -> !(t instanceof ASTAttributeUsage))) {
      Log.error(
          "The Port def " + node.getName() + " has sub elements that are not attribute usages this is not allowed.");
    }
    if(node.streamSpecializations().anyMatch(t -> !(t instanceof ASTSysMLSpecialization))) {
      Log.error("The Port def " + node.getName() + " has redefinitions or typings, this is not allowed.");
    }
    AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();
    var attributeUsageList = attributeResolveUtils.getAttributesOfElement(node);
    checkAttributes(node.getName(), attributeUsageList);
  }

  void checkAttributes(String nodeName, List<ASTAttributeUsage> attributesList) {

    if(attributesList.stream().count() > 2 | attributesList.stream().findAny().isEmpty()) {
      Log.error(
          "The Port " + nodeName + " has " + attributesList.stream().count()
              + " different attribute usages as sub elements. It has to use the attribute usage \"value\" and can use the attribute \"delayedPort\".");
    }
    var valueAttribute = attributesList.stream().filter(t -> t.getName().equals("value")).collect(Collectors.toList());
    var delayedAttribute = attributesList.stream().filter(t -> t.getName().equals("delayedPort")).collect(
        Collectors.toList());
    if(valueAttribute.size() + delayedAttribute.size() != attributesList.size()) {
      Log.error(
          "The Port " + nodeName + " has " + attributesList.stream().count()
              + " different attribute usages as sub elements. It has to use the attribute usage \"value\" and can use the attribute \"delayedPort\".");

    }
    if(valueAttribute.isEmpty() || valueAttribute.size() == 2) {
      Log.error(
          "The Port " + nodeName + " needs one sub element \"value\" but has " + valueAttribute.size() + ".");
    }
    if(!valueAttribute.get(0).isPresentSysMLFeatureDirection()) {
      Log.error(
          "The attribute usage \"value\" of port usage " + nodeName + " needs a feature direction but has none.");
    }

    if(!delayedAttribute.isEmpty()) {
      var astmcTypes = delayedAttribute.get(0).streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          t -> t.streamSuperTypes()).collect(
          Collectors.toList());
      if(!printName(astmcTypes.get(0)).equals("Boolean") || astmcTypes.size() != 1) {
        Log.error(
            "The Attribute usage " + delayedAttribute.get(0).getName() + " from port " + nodeName
                + " needs exactly one type, this type needs to be Boolean.");
      }
      if(!delayedAttribute.isEmpty()) {
        if(!delayedAttribute.get(0).isPresentExpression()) {
          Log.error(
              "The Attribute usage " + delayedAttribute.get(0).getName() + "of port "+ nodeName+" needs a default expression.");
        }
        var defaultValue = delayedAttribute.get(0).getExpression();
        if(!(defaultValue instanceof ASTLiteralExpression)) {
          Log.error(
              "The expression of delayPort of " + nodeName
                  + " needs to be a boolean literal.");
        }
        var literal = ((ASTLiteralExpression) defaultValue).getLiteral();
        if(!(literal instanceof ASTBooleanLiteral)) {
          Log.error(
              "The expression of delayPort of " + nodeName
                  + " needs to be a boolean literal.");
        }
        if(valueAttribute.get(0).getSysMLFeatureDirection() == ASTSysMLFeatureDirection.IN && ((ASTBooleanLiteral) literal).getValue()){
          Log.error(
              "The port has the direction IN and is delayed, this is not allowed.");
        }
      }
    }
  }

}
