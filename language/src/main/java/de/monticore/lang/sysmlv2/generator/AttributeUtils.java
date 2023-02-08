package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttributeUtils {

  GeneratorUtils generatorUtils;

  public AttributeUtils(){
    this.generatorUtils = new GeneratorUtils();
  }

  List<ASTCDAttribute> createAttributes(ASTSysMLElement astSysMLElement) {
    List<ASTAttributeUsage> attributeUsageList = createAttributeUsageList(astSysMLElement);
    List<String> generatedAttributeList = new ArrayList<>();
    //create astcdattributes for the current element
    List<ASTCDAttribute> attributeList = attributeUsageList.stream().map(
        t -> createAttribute(t, generatedAttributeList)).collect(
        Collectors.toList());
    attributeList.removeAll(Collections.singleton(null));
    Stream<ASTAttributeUsage> attributeUsageStream = null;
    //create astcdattributes for transitive attributes
    if(astSysMLElement instanceof ASTPartDef) {
      attributeUsageStream = ((ASTPartDef) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createAttributeUsageList(t).stream());
    }
    if(astSysMLElement instanceof ASTAttributeDef) {
      attributeUsageStream = ((ASTAttributeDef) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createAttributeUsageList(t).stream());
    }
    if(astSysMLElement instanceof ASTPartUsage) {
      attributeUsageStream = ((ASTPartUsage) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createAttributeUsageList(t).stream());
    }
    List<ASTCDAttribute> supertypeAttributeList = attributeUsageStream.map(
        f -> createAttribute(f, generatedAttributeList)).collect(
        Collectors.toList());

    supertypeAttributeList.removeAll(Collections.singleton(null));
    attributeList.addAll(supertypeAttributeList);
    return attributeList;
  }


  private List<ASTAttributeUsage> createAttributeUsageList(ASTSysMLElement element) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(element instanceof ASTPartDef)
      elementList = ((ASTPartDef) element).getSysMLElementList();
    if(element instanceof ASTPartUsage)
      elementList = ((ASTPartUsage) element).getSysMLElementList();
    if(element instanceof ASTAttributeDef)
      elementList = ((ASTAttributeDef) element).getSysMLElementList();
    List<ASTAttributeUsage> attributeUsageList;
    attributeUsageList = elementList.stream().filter(
        t -> t instanceof ASTAttributeUsage).map(t -> (ASTAttributeUsage) t).collect(Collectors.toList());
    return attributeUsageList;
  }

  ASTCDAttribute createAttribute(ASTSysMLElement element, List<String> stringList) {
    if(element instanceof ASTAttributeUsage) {
      String attributeName = ((ASTAttributeUsage) element).getName();
      if(!stringList.contains(attributeName)) {
        stringList.add(attributeName);
        ASTMCQualifiedType qualifiedType = generatorUtils.attributeType((ASTAttributeUsage) element);
        return CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
            CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      }
    }
    return null;
  }
}
