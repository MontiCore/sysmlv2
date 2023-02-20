package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

public class PortsVisitor implements SysMLPartsVisitor2 {

  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();

  @Override
  public void visit(ASTPortUsage node) {
    var attributes = attributeResolveUtils.getAttributesOfElement(node);
    var value = attributes.stream().filter(t -> t.getName().equals("value")).findFirst();
    var delayed = attributes.stream().filter(t -> t.getName().equals("delayed")).findFirst();
    if(value.isPresent() && countConjugations(node) >= 1) {
      node.setValueAttribute(switchDirection(value.get()));
    }
    else
      node.setValueAttribute(value.get());

    if(delayed.isPresent())
      node.setDelayedAttribute(delayed.get());
  }

  @Override
  public void visit(ASTPortDef node) {
    var attributes = attributeResolveUtils.getAttributesOfElement(node);
    var value = attributes.stream().filter(t -> t.getName().equals("value")).findFirst();
    var delayed = attributes.stream().filter(t -> t.getName().equals("delayed")).findFirst();
    if(value.isPresent())
      node.setValueAttribute(value.get());
    if(delayed.isPresent())
      node.setDelayedAttribute(delayed.get());
  }

  ASTAttributeUsage switchDirection(ASTAttributeUsage value) {
    var valueAttribute = value.deepClone();
    if(valueAttribute.getSysMLFeatureDirection().getIntValue() == ASTSysMLFeatureDirection.valueOf(
        "IN").getIntValue())
      valueAttribute.setSysMLFeatureDirection(
          ASTSysMLFeatureDirection.valueOf("OUT"));
    else {
      if(valueAttribute.getSysMLFeatureDirection().getIntValue() == ASTSysMLFeatureDirection.valueOf(
          "OUT").getIntValue())
        valueAttribute.setSysMLFeatureDirection(
            ASTSysMLFeatureDirection.valueOf("IN"));
    }
    return valueAttribute;
  }

  int countConjugations(ASTSysMLElement element) {

    if(element instanceof ASTPortUsage) {
      List<ASTSpecialization> specializations = ((ASTPortUsage) element).streamSpecializations().filter(
          t -> t instanceof ASTSysMLTyping).collect(
          Collectors.toList());

      var supertype = specializations.stream().flatMap(
          t -> t.streamSuperTypes()).map(
          t -> ((ASTPortUsage) element).getEnclosingScope().resolvePortDef(printName(t))).map(
          t -> t.get().getAstNode()).findFirst();
      if(supertype.isPresent()) {
        if(specializations.stream().anyMatch(t -> ((ASTSysMLTyping) t).isConjugated()))
          return 1 + countConjugations(supertype.get());
        return countConjugations(supertype.get());
      }
    }

    return 0;
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
