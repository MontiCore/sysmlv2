/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  protected GeneratorUtils generatorUtils;

  protected final GlobalExtensionManagement glex;

  PortUtils portUtils;

  public Parts2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                         ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
    this.portUtils = new PortUtils();
  }

  @Override
  public void visit(ASTAttributeDef astAttributeDef) {
    cdPackage = generatorUtils.initCdPackage(astAttributeDef, astcdDefinition, basePackage.getName());
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    ASTCDInterfaceUsage interfaceUsage = createInterfaceUsage(List.of(astAttributeDef));
    createInterface(astAttributeDef);
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder().setCDInterfaceUsage(interfaceUsage)
        .setName(astAttributeDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = createAttributes(astAttributeDef);
    partDefClass.setCDAttributeList(liste);
    generatorUtils.addMethods(partDefClass, liste, true, true);
    cdPackage.addCDElement(partDefClass);
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    cdPackage = generatorUtils.initCdPackage(astPartDef, astcdDefinition, basePackage.getName());
    ASTCDInterfaceUsage interfaceUsage = createInterfaceUsage(List.of(astPartDef));
    interfaceUsage.addInterface(createComponent());
    createInterface(astPartDef);
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder()
        .setName(astPartDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = createAttributes(astPartDef);
    liste.addAll(portUtils.createPorts(astPartDef));
    partDefClass.setCDAttributeList(liste);
    generatorUtils.addMethods(partDefClass, liste, true, true);
    portUtils.createComponentMethods(astPartDef, cd4C, partDefClass);
    cdPackage.addCDElement(partDefClass);
    stateToClassMap.put(astPartDef.getName(), partDefClass);
  }

  @Override
  public void visit(ASTPartUsage astPartUsage) {
    cdPackage = generatorUtils.initCdPackage(astPartUsage, astcdDefinition, basePackage.getName());

    var specializationList = astPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = astPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = astPartUsage.streamSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    if((!specializationList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (typingList.size() > 1
        | (!astPartUsage.getSysMLElementList().isEmpty()))) {
      //create class
      partDefClass = CD4CodeMill.cDClassBuilder()
          .setName(astPartUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      List<ASTCDAttribute> liste = createAttributes(astPartUsage);
      cdPackage.addCDElement(partDefClass);
      //create attributes
      partDefClass.setCDAttributeList(liste);
      generatorUtils.addMethods(partDefClass, liste, true, true);
      //create Interface usage
      List<ASTSysMLElement> sysMLElementList = new ArrayList<>();
      for (ASTMCType astmcType : typingList) {
        String name = astmcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
        var partDef = astPartUsage.getEnclosingScope().resolvePartDef(name);
        partDef.ifPresent(partDefSymbol -> sysMLElementList.add(partDefSymbol.getAstNode()));
      }
      ASTCDInterfaceUsage interfaceUsage = createInterfaceUsage(sysMLElementList);
      partDefClass.setCDInterfaceUsage(interfaceUsage);
      //create extends usage
      if(!specializationList.isEmpty()) {
        List<ASTMCType> extendList = new ArrayList<>();
        extendList.add(getNameOfSpecialication(specializationList.get(0), astPartUsage));
        ASTCDExtendUsage extendUsage = createExtendUsage(extendList, false);
        partDefClass.setCDExtendUsage(extendUsage);
      }
    }

  }

  ASTMCObjectType createComponent() {
    ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
        CD4CodeMill.mCQualifiedNameBuilder().
            addParts("de.monticore.lang.sysmlv2.generator.timesync.IComponent").build()).build();

    return mcQualifiedType;
  }

  ASTMCType getNameOfSpecialication(ASTMCType spec, ASTPartUsage astPartUsage) {
    ASTPartUsage specPartUsage = astPartUsage.getEnclosingScope().resolvePartUsage(printName(spec)).get().getAstNode();
    var specializationList = specPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = specPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = specPartUsage.streamSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    if((!specializationList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (typingList.size() > 1
        | (!specPartUsage.getSysMLElementList().isEmpty()))) {
      return spec;
    }
    if(!specializationList.isEmpty()) {
      return specializationList.get(0);
    }
    if(typingList.size() == 1) {
      return typingList.get(0);
    }
    return null;
  }

  ASTCDInterfaceUsage createInterfaceUsage(List<ASTSysMLElement> sysMLList) {
    //Step 1 get a list of all specializations
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().build();
    for (ASTSysMLElement sysMLElement : sysMLList) {
      String name = null;

      if(sysMLElement instanceof ASTPartDef) {
        name = ((ASTPartDef) sysMLElement).getName();
      }
      if(sysMLElement instanceof ASTAttributeDef) {
        name = ((ASTAttributeDef) sysMLElement).getName();
      }
      //Step 2 add the created interface to the InterfaceUsage
      ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          CD4CodeMill.mCQualifiedNameBuilder().
              addParts(name + "Interface").build()).build();
      interfaceUsage.addInterface(mcQualifiedType);
    }
    return interfaceUsage;
  }

  ASTCDExtendUsage createExtendUsage(List<ASTMCType> supertypeList, boolean extendsInterface) {
    String suffix = "";
    if(extendsInterface)
      suffix = "Interface";
    ASTCDExtendUsage extendUsage = CD4CodeMill.cDExtendUsageBuilder().build();

    //Step 2 add each specialization to the Extend Usage
    for (ASTMCType element : supertypeList) {
      String elementName = element.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));

      ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          CD4CodeMill.mCQualifiedNameBuilder().
              addParts(elementName + suffix).build()).build();
      extendUsage.addSuperclass(mcQualifiedType);
    }
    return extendUsage;
  }

  void createInterface(ASTSysMLElement sysMLElement) {
    String name = null;
    List<ASTSpecialization> specializationList = new ArrayList<>();
    if(sysMLElement instanceof ASTPartDef) {
      name = ((ASTPartDef) sysMLElement).getName();
      specializationList = ((ASTPartDef) sysMLElement).getSpecializationList();
    }
    if(sysMLElement instanceof ASTAttributeDef) {
      name = ((ASTAttributeDef) sysMLElement).getName();
      specializationList = ((ASTAttributeDef) sysMLElement).getSpecializationList();
    }
    List<ASTMCType> supertypeList = specializationList.stream().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(s -> s.streamSuperTypes()).collect(
        Collectors.toList());
    ASTCDExtendUsage extendUsage = createExtendUsage(supertypeList, true);
    ASTCDInterface partInterface = CD4CodeMill.cDInterfaceBuilder().setName(name + "Interface").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    if(!extendUsage.isEmptySuperclass()) {
      partInterface.setCDExtendUsage(extendUsage);
    }
    cdPackage.addCDElement(partInterface);

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

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
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

}
