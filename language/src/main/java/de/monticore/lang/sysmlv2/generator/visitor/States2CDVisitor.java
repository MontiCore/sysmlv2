/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator.visitor;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;
import de.monticore.lang.sysmlv2.generator.utils.AttributeUtils;
import de.monticore.lang.sysmlv2.generator.utils.ComponentUtils;
import de.monticore.lang.sysmlv2.generator.utils.GeneratorUtils;
import de.monticore.lang.sysmlv2.generator.utils.PartUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.StatesResolveUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.*;
import java.util.stream.Collectors;

public class States2CDVisitor implements SysMLStatesVisitor2 {

  public final static String ERROR_CODE = "0xDC012";

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDPackage cdPackage;

  protected static ASTCDPackage basePackage;

  protected ASTCDDefinition astcdDefinition;

  protected ASTCDClass stateUsageClass;

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  protected GeneratorUtils generatorUtils;

  ComponentUtils componentUtils;

  protected final GlobalExtensionManagement glex;

  PartUtils partUtils;

  AttributeUtils attributeUtils;

  StatesResolveUtils statesResolveUtils;

  AttributeResolveUtils attributeResolveUtils;

  public States2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                          ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
    this.partUtils = new PartUtils();
    this.componentUtils = new ComponentUtils();
    this.attributeUtils = new AttributeUtils();
    this.statesResolveUtils = new StatesResolveUtils();
    this.attributeResolveUtils = new AttributeResolveUtils();
  }

  @Override
  public void visit(ASTStateUsage astStateUsage) {
    cdPackage = generatorUtils.initCdPackage(astStateUsage, astcdDefinition, basePackage.getName());
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    if(astStateUsage.getIsAutomaton() && (parent instanceof ASTPartUsage || parent instanceof ASTPartDef)) {
      //create class
      stateUsageClass = CD4CodeMill.cDClassBuilder()
          .setName(astStateUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      //attributes
      String enumName = astStateUsage.getName() + "Enum";
      ASTMCQualifiedType qualifiedType = CD4CodeMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(
              List.of(enumName)).build()).build();
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      attributeList.add(CD4CodeMill.cDAttributeBuilder().setMCType(qualifiedType).setName(
          "currentState").setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build());
      ASTCDAttribute parentAttribute = createParentAttribute(astStateUsage);
      attributeList.add(parentAttribute);
      attributeList.addAll(createAttributeListSubStates(astStateUsage, ""));
      stateUsageClass.setCDAttributeList(attributeList);

      cdPackage.addCDElement(stateUsageClass);
      //create state enum
      var stateList = getStatesAndSubstates(astStateUsage);
      cdPackage.addCDElement(createEnum(astStateUsage));
      //add methods
      componentUtils.setPortLists((ASTSysMLElement) astStateUsage.getEnclosingScope().getAstNode());
      var inputPortsParent = componentUtils.inputPortList;
      var outputPortsParents = componentUtils.outputPortList;
      generatorUtils.addMethods(stateUsageClass, attributeList, true, true);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitMethod", stateList,
          enumName);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesCompute", stateList);
      cd4C.addConstructor(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesConstructor", astStateUsage,
          enumName, parentAttribute.printType());
      for (ASTStateUsage state :
          stateList) {

        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesTransition", state, astStateUsage,
            astStateUsage.getName() + "Enum", inputPortsParent, outputPortsParents);
        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesEntryAction", state, astStateUsage);
        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitAction", state, astStateUsage);

      }
    }
  }

  ASTCDEnum createEnum(ASTStateUsage astStateUsage) {

    return CD4CodeMill.cDEnumBuilder().setName(astStateUsage.getName() + "Enum").setCDEnumConstantsList(
        createConstantList(astStateUsage, "")).setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
  }

  List<ASTCDEnumConstant> createConstantList(ASTStateUsage astStateUsage, String prefix) {
    var stateList = statesResolveUtils.getStatesOfElement(astStateUsage);
    var subAutomatonList = stateList.stream().filter(t -> t.getIsAutomaton()).collect(
        Collectors.toList());
    List<ASTCDEnumConstant> enumConstantList = stateList.stream().map(
        state -> CD4CodeMill.cDEnumConstantBuilder().setName(prefix + state.getName()).build()).collect(
        Collectors.toList());
    var constantsSubAutomaton = subAutomatonList.stream().flatMap(
        t -> createConstantList(t, prefix + t.getName() + "_").stream()).collect(Collectors.toList());
    enumConstantList.addAll(constantsSubAutomaton);
    return enumConstantList;
  }

  List<ASTCDAttribute> createAttributeListSubStates(ASTStateUsage astStateUsage, String prefix) {
    var attributeUsageList = attributeResolveUtils.getAttributesOfElement(astStateUsage);
    var subAutomatonList = statesResolveUtils.getStatesOfElement(astStateUsage).stream().filter(
        t -> t.getIsAutomaton()).collect(
        Collectors.toList());

    List<ASTCDAttribute> astcdAttributeList = attributeUsageList.stream().map(
        attributeUsage -> attributeUtils.createAttributeWithPrefix(attributeUsage, prefix)).collect(
        Collectors.toList());
    var attributesSubAutomaton = subAutomatonList.stream().flatMap(
        t -> createAttributeListSubStates(t, prefix + t.getName() + "_").stream()).collect(
        Collectors.toList());
    astcdAttributeList.addAll(attributesSubAutomaton);
    return astcdAttributeList;
  }

  ASTCDAttribute createParentAttribute(ASTStateUsage astStateUsage) {
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    ASTMCQualifiedType type = null;
    if(parent instanceof ASTPartUsage) {
      type = partUtils.partType((ASTPartUsage) parent);
    }
    else if(parent instanceof ASTPartDef) {
      type = generatorUtils.qualifiedType(((ASTPartDef) parent).getName());
    }
    return CD4CodeMill.cDAttributeBuilder().setName("parentPart").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(type).build();
  }

  List<ASTStateUsage> getStatesAndSubstates(ASTStateUsage stateUsages) {
    var stateList = statesResolveUtils.getStatesOfElement(stateUsages);
    var subAutomatonList = stateList.stream().filter(
        t -> t.getIsAutomaton()).collect(
        Collectors.toList());
    var statesSubAutomaton = subAutomatonList.stream().flatMap(t -> getStatesAndSubstates(t).stream()).collect(
        Collectors.toList());
    stateList.addAll(statesSubAutomaton);
    return stateList;
  }

}
