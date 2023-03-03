package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDAttributeBuilder;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.visitor.ExpressionRenameVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.List;
import java.util.stream.Collectors;

public class AttributeUtils {

  GeneratorUtils generatorUtils;

  public AttributeUtils() {
    this.generatorUtils = new GeneratorUtils();
  }

  static public List<ASTCDAttribute> createAttributes(ASTSysMLElement astSysMLElement) {
    List<ASTAttributeUsage> attributeUsageList = AttributeResolveUtils.getAttributesOfElement(astSysMLElement);
    //create astcdattributes for the current element
    return attributeUsageList.stream().map(
        AttributeUtils::createAttribute).collect(
        Collectors.toList());
  }

  static ASTCDAttribute createAttribute(ASTSysMLElement element) {
    if(element instanceof ASTAttributeUsage) {
      ASTCDAttributeBuilder attributeBuilder = CD4CodeMill.cDAttributeBuilder();
      String attributeName = ((ASTAttributeUsage) element).getName();
      if(((ASTAttributeUsage) element).isPresentExpression()) {
        attributeBuilder.setInitial(((ASTAttributeUsage) element).getExpression());
      }
      ASTMCQualifiedType qualifiedType = GeneratorUtils.attributeType((ASTAttributeUsage) element);
      return attributeBuilder.setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();

    }
    return null;
  }

  static public ASTCDAttribute createAttributeWithPrefix(ASTSysMLElement attribute, String prefix,
                                                  ASTSysMLElement parentPart) {
    if(attribute instanceof ASTAttributeUsage) {
      String attributeName = prefix + ((ASTAttributeUsage) attribute).getName();
      ASTCDAttributeBuilder attributeBuilder = CD4CodeMill.cDAttributeBuilder();
      if(((ASTAttributeUsage) attribute).isPresentExpression()) {
        var expr = ((ASTAttributeUsage) attribute).getExpression();
        ExpressionRenameVisitor visitor = new ExpressionRenameVisitor((ASTAttributeUsage) attribute, attributeName,
            AttributeResolveUtils.getAttributesOfElement(parentPart));
        SysMLv2Traverser sysMLv2Traverser = SysMLv2Mill.traverser();
        sysMLv2Traverser.add4ExpressionsBasis(visitor);
        var parent = attribute.getEnclosingScope().getAstNode();
        parent.accept(sysMLv2Traverser);
        attributeBuilder.setInitial(expr);
      }
      ASTMCQualifiedType qualifiedType = GeneratorUtils.attributeType((ASTAttributeUsage) attribute);
      return attributeBuilder.setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
    }
    return null;
  }
}
