/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.visitor;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.generator.utils.ActionsUtils;
import de.monticore.lang.sysmlv2.generator.utils.AttributeUtils;
import de.monticore.lang.sysmlv2.generator.utils.ComponentUtils;
import de.monticore.lang.sysmlv2.generator.utils.PackageUtils;
import de.monticore.lang.sysmlv2.generator.utils.InterfaceUtils;
import de.monticore.lang.sysmlv2.generator.utils.PartUtils;
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

  protected final GlobalExtensionManagement glex;

  ComponentUtils componentUtils;

  ActionsUtils actionsUtils = new ActionsUtils();

  public Parts2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                         ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    Parts2CDVisitor.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.componentUtils = new ComponentUtils();
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    // Step 0: Init Package
    cdPackage = PackageUtils.initCdPackage(astPartDef, astcdDefinition,
        basePackage.getName());
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    ASTCDInterfaceUsage interfaceUsage = InterfaceUtils
        .createInterfaceUsage(List.of(astPartDef));
    interfaceUsage.addInterface(componentUtils.createComponent());
    cdPackage.addCDElement(InterfaceUtils
        .createInterface(astPartDef));
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder()
        .setName(astPartDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build())
        .setCDInterfaceUsage(interfaceUsage).build();
    cdPackage.addCDElement(partDefClass);
    //Step 3 create Attributes
    List<ASTCDAttribute> attributeList = AttributeUtils
        .createAttributes(astPartDef);
    attributeList.addAll(componentUtils.createPorts(astPartDef));
    attributeList.addAll(PartUtils
        .createPartsAsAttributes(astPartDef));
    attributeList.addAll(handleBehaviour(astPartDef));
    partDefClass.setCDAttributeList(attributeList);
    //Step 4 create Methods
    actionsUtils.createActionsForPart(astPartDef, partDefClass);
    PackageUtils.addMethods(partDefClass, attributeList,
        true, true);
    componentUtils.createComponentMethods(astPartDef, cd4C,
        partDefClass);
  }

  @Override
  public void visit(ASTPartUsage astPartUsage) {
    // step 0 check if adhoc class definiton, if not do nothing
    if(PartUtils.isAdHocClassDefinition(astPartUsage)) {
      //step 1 init Package
      cdPackage = PackageUtils.initCdPackage(astPartUsage,
          astcdDefinition, basePackage.getName());
      //step 2 create class
      partDefClass = CD4CodeMill.cDClassBuilder()
          .setName(astPartUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC()
          .build()).build();
      cdPackage.addCDElement(partDefClass);

      //Step 3 create Interface usage
      ASTCDInterfaceUsage interfaceUsage =
          createTypingInterfaceUsage(astPartUsage);
      interfaceUsage.addInterface(componentUtils.createComponent());
      partDefClass.setCDInterfaceUsage(interfaceUsage);
      //step 4 create extends usage
      initExtendForPartUsage(astPartUsage);
      //step 5 create attributes
      List<ASTCDAttribute> attributeList = AttributeUtils.
          createAttributes(astPartUsage);
      attributeList.addAll(componentUtils.createPorts(astPartUsage));
      attributeList.addAll(PartUtils.createPartsAsAttributes(astPartUsage));
      attributeList.addAll(handleBehaviour(astPartUsage));
      partDefClass.setCDAttributeList(attributeList);
      //step 6 create Methods
      componentUtils.createComponentMethods(astPartUsage, cd4C,
          partDefClass);
      PackageUtils.addMethods(partDefClass, attributeList,
          true, true);

      actionsUtils.createActionsForPart(astPartUsage, partDefClass);

    }

  }

  void initExtendForPartUsage(ASTPartUsage astPartUsage) {
    var subsetList = astPartUsage.streamUsageSpecializations().filter(
        t -> t instanceof ASTSysMLSubsetting).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    if(!subsetList.isEmpty()) {
      List<ASTMCType> extendList = new ArrayList<>();
      extendList.add(PartUtils.getNameOfSubsetPart(subsetList.get(0), astPartUsage));
      ASTCDExtendUsage extendUsage = InterfaceUtils.createExtendUsage(extendList, false);
      partDefClass.setCDExtendUsage(extendUsage);
    }

  }

  private ASTCDInterfaceUsage createTypingInterfaceUsage(ASTPartUsage astPartUsage) {
    var typingList = astPartUsage.streamUsageSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    ASTCDInterfaceUsage interfaceUsage;
    List<ASTSysMLElement> sysMLElementList = new ArrayList<>();
    for (ASTMCType astmcType : typingList) {
      String name = astmcType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
      var partDef = astPartUsage.getEnclosingScope().resolvePartDef(name);
      partDef.ifPresent(partDefSymbol -> sysMLElementList.add(partDefSymbol.getAstNode()));
    }
    interfaceUsage = InterfaceUtils.createInterfaceUsage(sysMLElementList);
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
      ASTMCQualifiedType qualifiedType = PackageUtils.qualifiedType(attributeName);
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
      ASTMCQualifiedType qualifiedType = PackageUtils.qualifiedType(attributeName);
      var automaton = CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      attributeList.add(automaton);
    }
    return attributeList;
  }
  // Support methods

}
