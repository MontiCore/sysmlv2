/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parts2CDVisitor implements SysMLPartsVisitor2 {

  public final static String ERROR_CODE = "0xDC012";

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDPackage cdPackage;

  protected static ASTCDPackage basePackage;

  protected ASTCDDefinition astcdDefinition;

  protected ASTCDClass partDefClass;

  /**
   * Mapping of the state implementation classes for every state
   */
  protected final Map<String, ASTCDClass> stateToClassMap = new HashMap<>();

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  protected final GlobalExtensionManagement glex;

  public Parts2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                         ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
  }

  @Override
  public void visit(ASTAttributeDef astAttributeDef) {
    initCdPackage(astAttributeDef);
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    ASTCDInterfaceUsage interfaceUsage = createInterfaceUsage(astAttributeDef);
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder().setCDInterfaceUsage(interfaceUsage)
        .setName(astAttributeDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = createAttributes(astAttributeDef);

    partDefClass.setCDAttributeList(liste);
    cdPackage.addCDElement(partDefClass);
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    initCdPackage(astPartDef);
    ASTCDInterfaceUsage interfaceUsage = createInterfaceUsage(astPartDef);
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder()
        .setName(astPartDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = createAttributes(astPartDef);

    partDefClass.setCDAttributeList(liste);
    cdPackage.addCDElement(partDefClass);
    stateToClassMap.put(astPartDef.getName(), partDefClass);
  }

  ASTCDInterfaceUsage createInterfaceUsage(ASTSysMLElement sysMLElement) {
    //Step 1 get a list of all specializations
    ASTCDExtendUsage extendUsage = CD4CodeMill.cDExtendUsageBuilder().build();
    List<ASTSpecialization> specializationList = new ArrayList<>();
    String name = null;
    if(sysMLElement instanceof ASTPartDef) {
      specializationList = ((ASTPartDef) sysMLElement).getSpecializationList();
      name = ((ASTPartDef) sysMLElement).getName();
    }
    if(sysMLElement instanceof ASTAttributeDef) {
      specializationList = ((ASTAttributeDef) sysMLElement).getSpecializationList();
      name = ((ASTAttributeDef) sysMLElement).getName();
    }
    List<ASTMCType> supertypeList = specializationList.stream().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(s -> s.streamSuperTypes()).collect(
        Collectors.toList());
    //Step 2 add each specialization to the Extend Usage
    for (ASTMCType element : supertypeList) {
      String elementName = element.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));

      ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          CD4CodeMill.mCQualifiedNameBuilder().
              addParts(elementName + "Interface").build()).build();
      extendUsage.addSuperclass(mcQualifiedType);
    }
    //Step 3 create the interface

    ASTCDInterface partInterface = CD4CodeMill.cDInterfaceBuilder().setName(name + "Interface").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    if(!extendUsage.isEmptySuperclass()) {
      partInterface.setCDExtendUsage(extendUsage);
    }
    cdPackage.addCDElement(partInterface);

    //Step 4 add the created interface to the InterfaceUsage
    ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
        CD4CodeMill.mCQualifiedNameBuilder().
            addParts(name + "Interface").build()).build();
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().build();
    interfaceUsage.addInterface(mcQualifiedType);

    return interfaceUsage;
  }

  List<ASTCDAttribute> createAttributes(ASTPartDef astPartDef) {
    List<ASTAttributeUsage> attributeUsageList = createAttributeUsageList(astPartDef);
    List<String> generatedAttributeList = new ArrayList<>();
    List<ASTCDAttribute> attributeList = attributeUsageList.stream().map(
        t -> createAttribute(t, generatedAttributeList)).collect(
        Collectors.toList());
    attributeList.removeAll(Collections.singleton(null));
    List<ASTCDAttribute> supertypeAttributeList = astPartDef.streamTransitiveDefSupertypes().flatMap(
        t -> createAttributeUsageList(t).stream()).map(f -> createAttribute(f, generatedAttributeList)).collect(
        Collectors.toList());
    supertypeAttributeList.removeAll(Collections.singleton(null));
    attributeList.addAll(supertypeAttributeList);
    return attributeList;
  }

  List<ASTCDAttribute> createAttributes(ASTAttributeDef astAttributeDef) {
    List<ASTAttributeUsage> attributeUsageList = createAttributeUsageList(astAttributeDef);
    List<String> generatedAttributeList = new ArrayList<>();
    List<ASTCDAttribute> attributeList = attributeUsageList.stream().map(
        t -> createAttribute(t, generatedAttributeList)).collect(
        Collectors.toList());
    attributeList.removeAll(Collections.singleton(null));
    List<ASTCDAttribute> supertypeAttributeList = astAttributeDef.streamTransitiveDefSupertypes().flatMap(
        t -> createAttributeUsageList(t).stream()).map(f -> createAttribute(f, generatedAttributeList)).collect(
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
        ASTMCQualifiedType qualifiedType = attributeType((ASTAttributeUsage) element);
        return CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
            CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      }
    }
    return null;
  }

  public ASTCDCompilationUnit getCdCompilationUnit() {
    return cdCompilationUnit;
  }

  public ASTCDClass getScClass() {
    return partDefClass;
  }

  public Map<String, ASTCDClass> getStateToClassMap() {
    return stateToClassMap;
  }

  // Support methods
  protected ASTMCQualifiedType attributeType(ASTAttributeUsage element) {
    var sysMLTypingList = element.getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
    String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
        new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
    List<String> partsList = Splitters.DOT.splitToList(typString);
    String typeName = partsList.get(partsList.size() - 1);
    if(typeName.equals("Boolean"))
      partsList = List.of("boolean");
    if(typeName.equals("Real"))
      partsList = List.of("float");
    return qualifiedType(partsList);
  }

  protected ASTMCQualifiedType qualifiedType(String qname) {
    return qualifiedType(Splitters.DOT.splitToList(qname));
  }

  protected ASTMCQualifiedType qualifiedType(List<String> partsList) {
    return CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(partsList).build()).build();
  }

  void initCdPackage(ASTSysMLElement element) {
    List<String> partList = List.of(basePackage.getName());
    List<String> testListe = initCdPackage(element, partList);
    cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder()
            .setPartsList(testListe)
            .build())
        .build();
    astcdDefinition.addCDElement(cdPackage);
  }

  List<String> initCdPackage(ASTSysMLElement element, List<String> partList) {
    List<String> test = new ArrayList<>(partList);
    var hoi = element.getEnclosingScope().getAstNode();
    if(hoi instanceof ASTSysMLElement) {
      ASTSysMLElement astSysMLElement = (ASTSysMLElement) hoi;
      if(astSysMLElement instanceof ASTSysMLPackage) {
        var halls = ((ASTSysMLPackage) astSysMLElement).getName().toLowerCase();

        test.add(halls);

        initCdPackage(astSysMLElement, test);
      }
      else if(!(astSysMLElement instanceof SysMLv2ArtifactScope)) {
        initCdPackage(element, partList);
      }
      else {
        return test;
      }
    }
    return test;
  }
}
