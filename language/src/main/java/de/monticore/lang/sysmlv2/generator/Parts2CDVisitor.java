/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
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

  protected GeneratorUtils generatorUtils;

  protected final GlobalExtensionManagement glex;

  PortUtils portUtils;

  PartUtils partUtils;

  InterfaceUtils interfaceUtils;
  AttributeUtils attributeUtils;

  public Parts2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                         ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
    this.portUtils = new PortUtils();
    this.partUtils = new PartUtils();
    this.interfaceUtils = new InterfaceUtils();
    this.attributeUtils = new AttributeUtils();
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    cdPackage = generatorUtils.initCdPackage(astPartDef, astcdDefinition, basePackage.getName());
    ASTCDInterfaceUsage interfaceUsage = interfaceUtils.createInterfaceUsage(List.of(astPartDef));
    interfaceUsage.addInterface(createComponent());
    cdPackage.addCDElement(interfaceUtils.createInterface(astPartDef));
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder()
        .setName(astPartDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = attributeUtils.createAttributes(astPartDef);
    liste.addAll(portUtils.createPorts(astPartDef));
    partDefClass.setCDAttributeList(liste);
    generatorUtils.addMethods(partDefClass, liste, true, true);
    portUtils.createComponentMethods(astPartDef, cd4C, partDefClass, partUtils.setPortLists(astPartDef));
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
    if(partUtils.isAdHocClassDefinition(astPartUsage)) {
      //create class
      partDefClass = CD4CodeMill.cDClassBuilder()
          .setName(astPartUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      List<ASTCDAttribute> liste = attributeUtils.createAttributes(astPartUsage);
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
      ASTCDInterfaceUsage interfaceUsage = interfaceUtils.createInterfaceUsage(sysMLElementList);
      partDefClass.setCDInterfaceUsage(interfaceUsage);
      //create extends usage
      if(!specializationList.isEmpty()) {
        List<ASTMCType> extendList = new ArrayList<>();
        extendList.add(partUtils.getNameOfSpecialication(specializationList.get(0), astPartUsage));
        ASTCDExtendUsage extendUsage = interfaceUtils.createExtendUsage(extendList, false);
        partDefClass.setCDExtendUsage(extendUsage);
      }
    }

  }

  ASTMCObjectType createComponent() {

    return CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
        CD4CodeMill.mCQualifiedNameBuilder().
            addParts("de.monticore.lang.sysmlv2.generator.timesync.IComponent").build()).build();
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
