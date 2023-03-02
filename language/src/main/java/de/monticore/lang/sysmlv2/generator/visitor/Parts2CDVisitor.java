/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.visitor;

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
import de.monticore.lang.sysmlv2.generator.utils.ActionsUtils;
import de.monticore.lang.sysmlv2.generator.utils.AttributeUtils;
import de.monticore.lang.sysmlv2.generator.utils.ComponentUtils;
import de.monticore.lang.sysmlv2.generator.utils.GeneratorUtils;
import de.monticore.lang.sysmlv2.generator.utils.InterfaceUtils;
import de.monticore.lang.sysmlv2.generator.utils.PartUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PartResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parts2CDVisitor implements SysMLPartsVisitor2 {

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDPackage cdPackage;

  protected static ASTCDPackage basePackage;

  protected ASTCDDefinition astcdDefinition;

  protected ASTCDClass partDefClass;

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  protected GeneratorUtils generatorUtils;

  protected final GlobalExtensionManagement glex;

  ComponentUtils componentUtils;

  PartUtils partUtils;

  InterfaceUtils interfaceUtils;

  AttributeUtils attributeUtils;

  PartResolveUtils partResolveUtils;

  AttributeResolveUtils attributeResolveUtils;

  ActionsUtils actionsUtils = new ActionsUtils();

  public Parts2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                         ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
    this.componentUtils = new ComponentUtils();
    this.partUtils = new PartUtils();
    this.interfaceUtils = new InterfaceUtils();
    this.attributeUtils = new AttributeUtils();
    this.partResolveUtils = new PartResolveUtils();
    this.attributeResolveUtils = new AttributeResolveUtils();
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    // Step 0: Init Package
    cdPackage = generatorUtils.initCdPackage(astPartDef, astcdDefinition, basePackage.getName());
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    ASTCDInterfaceUsage interfaceUsage = interfaceUtils.createInterfaceUsage(List.of(astPartDef));
    interfaceUsage.addInterface(componentUtils.createComponent());
    cdPackage.addCDElement(interfaceUtils.createInterface(astPartDef));
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder()
        .setName(astPartDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    cdPackage.addCDElement(partDefClass);
    //Step 3 create Attributes
    List<ASTCDAttribute> attributeList = attributeUtils.createAttributes(astPartDef);
    attributeList.addAll(componentUtils.createPorts(astPartDef));
    attributeList.addAll(partUtils.createPartsAsAttributes(astPartDef));
    attributeList.addAll(handleBehaviour(astPartDef));
    partDefClass.setCDAttributeList(attributeList);
    //Step 4 create Methods
    actionsUtils.createActionsForPart(astPartDef,partDefClass);
    generatorUtils.addMethods(partDefClass, attributeList, true, true);
    componentUtils.createComponentMethods(astPartDef, cd4C, partDefClass,
        partResolveUtils.getPartUsageOfNode(astPartDef),
        attributeResolveUtils.getAttributesOfElement(astPartDef));


  }

  @Override
  public void visit(ASTPartUsage astPartUsage) {
    //step 0 init Package
    cdPackage = generatorUtils.initCdPackage(astPartUsage, astcdDefinition, basePackage.getName());
    // step 1 check if adhoc class definiton, if not do nothing
    if(partUtils.isAdHocClassDefinition(astPartUsage)) {
      //step 2 create class
      partDefClass = CD4CodeMill.cDClassBuilder()
          .setName(astPartUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      cdPackage.addCDElement(partDefClass);

      //Step 3 create Interface usage
      ASTCDInterfaceUsage interfaceUsage = createTypingInterfaceUsage(astPartUsage);
      interfaceUsage.addInterface(componentUtils.createComponent());
      partDefClass.setCDInterfaceUsage(interfaceUsage);
      //step 4 create extends usage
      initExtendForPartUsage(astPartUsage);
      //step 5 create attributes
      List<ASTCDAttribute> attributeList = attributeUtils.createAttributes(astPartUsage);
      attributeList.addAll(componentUtils.createPorts(astPartUsage));
      attributeList.addAll(partUtils.createPartsAsAttributes(astPartUsage));
      attributeList.addAll(handleBehaviour(astPartUsage));
      partDefClass.setCDAttributeList(attributeList);
      //step 6 create Methods
      componentUtils.createComponentMethods(astPartUsage, cd4C, partDefClass,
          partResolveUtils.getPartUsageOfNode(astPartUsage),
          attributeResolveUtils.getAttributesOfElement(astPartUsage));
      generatorUtils.addMethods(partDefClass, attributeList, true, true);

      actionsUtils.createActionsForPart(astPartUsage,partDefClass);

    }

  }

  void initExtendForPartUsage(ASTPartUsage astPartUsage) {
    var specializationList = astPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    if(!specializationList.isEmpty()) {
      List<ASTMCType> extendList = new ArrayList<>();
      extendList.add(partUtils.getNameOfSpecialication(specializationList.get(0), astPartUsage));
      ASTCDExtendUsage extendUsage = interfaceUtils.createExtendUsage(extendList, false);
      partDefClass.setCDExtendUsage(extendUsage);
    }

  }

  private ASTCDInterfaceUsage createTypingInterfaceUsage(ASTPartUsage astPartUsage) {
    var typingList = astPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    ASTCDInterfaceUsage interfaceUsage;
    List<ASTSysMLElement> sysMLElementList = new ArrayList<>();
    for (ASTMCType astmcType : typingList) {
      String name = astmcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
      var partDef = astPartUsage.getEnclosingScope().resolvePartDef(name);
      partDef.ifPresent(partDefSymbol -> sysMLElementList.add(partDefSymbol.getAstNode()));
    }
    interfaceUsage = interfaceUtils.createInterfaceUsage(sysMLElementList);
    return interfaceUsage;
  }

  public ASTCDCompilationUnit getCdCompilationUnit() {
    return cdCompilationUnit;
  }

  public ASTCDClass getScClass() {
    return partDefClass;
  }

  List<ASTCDAttribute> handleBehaviour(ASTPartUsage element) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    if(element.hasAutomaton()) {
      String attributeName = element.getAutomaton().getName();
      ASTMCQualifiedType qualifiedType = generatorUtils.qualifiedType(attributeName);
      var automaton = CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      attributeList.add(automaton);
    }
    return attributeList;
  }

  List<ASTCDAttribute> handleBehaviour(ASTPartDef element) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    if(element.hasAutomaton()) {
      String attributeName = element.getAutomaton().getName();
      ASTMCQualifiedType qualifiedType = generatorUtils.qualifiedType(attributeName);
      var automaton = CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      attributeList.add(automaton);
    }
    return attributeList;
  }
  // Support methods

}
