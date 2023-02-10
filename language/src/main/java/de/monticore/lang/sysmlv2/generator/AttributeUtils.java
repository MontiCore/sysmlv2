package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.List;
import java.util.stream.Collectors;

public class AttributeUtils {

  GeneratorUtils generatorUtils;

  AttributeResolveUtils attributeResolveUtils;

  public AttributeUtils() {
    this.generatorUtils = new GeneratorUtils();
    this.attributeResolveUtils = new AttributeResolveUtils();
  }

  List<ASTCDAttribute> createAttributes(ASTSysMLElement astSysMLElement) {
    List<ASTAttributeUsage> attributeUsageList = attributeResolveUtils.getAttributesOfElement(astSysMLElement);
    //create astcdattributes for the current element
    return attributeUsageList.stream().map(
        t -> createAttribute(t)).collect(
        Collectors.toList());
  }

  ASTCDAttribute createAttribute(ASTSysMLElement element) {
    if(element instanceof ASTAttributeUsage) {
      String attributeName = ((ASTAttributeUsage) element).getName();

      ASTMCQualifiedType qualifiedType = generatorUtils.attributeType((ASTAttributeUsage) element);
      return CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();

    }
    return null;
  }
}
