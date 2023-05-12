package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDAttributeBuilder;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.visitor.ExpressionRenameVisitor;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Splitters;

import java.util.List;
import java.util.stream.Collectors;

public class AttributeUtils {

  PackageUtils packageUtils;

  public AttributeUtils() {
    this.packageUtils = new PackageUtils();
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
      ASTMCQualifiedType qualifiedType = ScalarValues.attributeType((ASTAttributeUsage) element);
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
      ASTMCQualifiedType qualifiedType = ScalarValues.attributeType((ASTAttributeUsage) attribute);
      return attributeBuilder.setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
    }
    return null;
  }

    static public ASTMCQualifiedType attributeType(ASTAttributeUsage element) {
      var sysMLTypingList = element.getUsageSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
      String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
          new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
      List<String> partsList = Splitters.DOT.splitToList(typString);
      String typeName = partsList.get(partsList.size() - 1);
      if(ScalarValues.scalarValueMapping.containsKey(typeName))
        partsList = List.of(ScalarValues.scalarValueMapping.get(typeName));
      return PackageUtils.qualifiedType(partsList);
    }
}
