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
import de.monticore.lang.sysmlv2.generator.helper.AutomatonHelper;
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
  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDPackage cdPackage;

  protected static ASTCDPackage basePackage;

  protected ASTCDDefinition astcdDefinition;

  protected ASTCDClass stateUsageClass;

  /**
   * Code template reference
   */
  protected final CD4C cd4C;

  ComponentUtils componentUtils;

  protected final GlobalExtensionManagement glex;

  AutomatonHelper automatonHelper = new AutomatonHelper();

  public States2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                          ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    States2CDVisitor.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.componentUtils = new ComponentUtils();
  }

  @Override
  public void visit(ASTStateUsage astStateUsage) {

    ASTSysMLElement parentPart = getParentPart(astStateUsage);
    cdPackage = GeneratorUtils.initCdPackage(astStateUsage, astcdDefinition, basePackage.getName());
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    if(astStateUsage.getIsAutomaton() && (parent instanceof ASTPartUsage || parent instanceof ASTPartDef)) {
      //create class
      stateUsageClass = CD4CodeMill.cDClassBuilder()
          .setName(astStateUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      cdPackage.addCDElement(stateUsageClass);
      //attributes
      List<ASTStateUsage> subAutomatons = getSubautomatons(astStateUsage);
      List<ASTCDAttribute> attributeList = new ArrayList<>(
          createCurrentStateAttributeList(astStateUsage, subAutomatons));
      ASTCDAttribute parentAttribute = createParentAttribute(astStateUsage);
      attributeList.add(parentAttribute);
      attributeList.addAll(createAttributeListSubStates(astStateUsage, "", parentPart));
      stateUsageClass.setCDAttributeList(attributeList);

      //create state enum
      var stateList = StatesResolveUtils.getStatesOfElement(astStateUsage);
      cdPackage.addCDElement(createEnum(astStateUsage));
      stateList.stream().filter(ASTStateUsage::getIsAutomaton).forEach(
          t -> cdPackage.addCDElement(createEnum(t)));

      //add methods

      GeneratorUtils.addMethods(stateUsageClass, attributeList, true, true);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesCompute", stateList, astStateUsage);
      cd4C.addConstructor(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesConstructor", astStateUsage,
          parentAttribute.printType());
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesEntryAction", astStateUsage, astStateUsage,
          parentPart);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitAction", astStateUsage, astStateUsage,
          parentPart);
      componentUtils.setPortLists((ASTSysMLElement) astStateUsage.getEnclosingScope().getAstNode());
      createTransitionsForStateList(stateList, astStateUsage, parentPart);

    }
  }

  ASTCDEnum createEnum(ASTStateUsage astStateUsage) {
    var stateList = StatesResolveUtils.getStatesOfElement(astStateUsage);
    List<ASTCDEnumConstant> enumConstantList = stateList.stream().map(
        state -> CD4CodeMill.cDEnumConstantBuilder().setName(state.getName()).build()).collect(
        Collectors.toList());
    return CD4CodeMill.cDEnumBuilder().setName(astStateUsage.getName() + "Enum").setCDEnumConstantsList(
        enumConstantList).setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
  }

  List<ASTCDAttribute> createAttributeListSubStates(ASTStateUsage astStateUsage, String prefix,
                                                    ASTSysMLElement parent) {
    var attributeUsageList = AttributeResolveUtils.getAttributesOfElement(astStateUsage);
    var subAutomatonList = StatesResolveUtils.getStatesOfElement(astStateUsage).stream().filter(
        ASTStateUsage::getIsAutomaton).collect(
        Collectors.toList());

    List<ASTCDAttribute> astcdAttributeList = attributeUsageList.stream().map(
        attributeUsage -> AttributeUtils.createAttributeWithPrefix(attributeUsage, prefix, parent)).collect(
        Collectors.toList());
    var attributesSubAutomaton = subAutomatonList.stream().flatMap(
        t -> createAttributeListSubStates(t, prefix + t.getName() + "_", parent).stream()).collect(
        Collectors.toList());
    astcdAttributeList.addAll(attributesSubAutomaton);
    return astcdAttributeList;
  }

  ASTCDAttribute createParentAttribute(ASTStateUsage astStateUsage) {
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    ASTMCQualifiedType type = null;
    if(parent instanceof ASTPartUsage) {
      type = PartUtils.partType((ASTPartUsage) parent);
    }
    else if(parent instanceof ASTPartDef) {
      type = GeneratorUtils.qualifiedType(((ASTPartDef) parent).getName());
    }
    return CD4CodeMill.cDAttributeBuilder().setName("parentPart").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(type).build();
  }

  ASTSysMLElement getParentPart(ASTStateUsage astStateUsage) {
    var parent = astStateUsage.getEnclosingScope().getAstNode();
    if(parent instanceof ASTPartUsage || parent instanceof ASTPartDef) {
      return (ASTSysMLElement) parent;
    }
    if(parent instanceof ASTStateUsage) {
      return getParentPart((ASTStateUsage) parent);
    }
    return null;
  }

  List<ASTCDAttribute> createCurrentStateAttributeList(ASTStateUsage parentAutomaton,
                                                       List<ASTStateUsage> subAutomatons) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    attributeList.add(createCurrentStateAttribute(automatonHelper.resolveEnumName(parentAutomaton),
        automatonHelper.resolveCurrentStateName(parentAutomaton)));
    attributeList.addAll(subAutomatons.stream().map(
        t -> createCurrentStateAttribute(automatonHelper.resolveEnumName(t),
            automatonHelper.resolveCurrentStateName(t))).collect(
        Collectors.toList()));
    return attributeList;
  }

  ASTCDAttribute createCurrentStateAttribute(String enumName, String attributeName) {
    ASTMCQualifiedType qualifiedType = CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(
            List.of(enumName)).build()).build();
    return CD4CodeMill.cDAttributeBuilder().setMCType(qualifiedType).setName(
        attributeName).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
  }

  List<ASTStateUsage> getSubautomatons(ASTStateUsage parent) {
    var stateList = StatesResolveUtils.getStatesOfElement(parent);
    var subAutomatonList = stateList.stream().filter(ASTStateUsage::getIsAutomaton).collect(Collectors.toList());
    var subStatesOfSubstates = subAutomatonList.stream().flatMap(t -> getSubautomatons(t).stream()).collect(
        Collectors.toList());
    subAutomatonList.addAll(subStatesOfSubstates);
    return subAutomatonList;
  }

  void createTransitionsForStateList(List<ASTStateUsage> stateList, ASTStateUsage astStateUsage,
                                     ASTSysMLElement parentPart) {
    var inputPortsParent = componentUtils.inputPortList;
    var outputPortsParent = componentUtils.outputPortList;

    for (ASTStateUsage state :
        stateList) {

      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesTransitionFrom", state, astStateUsage,
          inputPortsParent, outputPortsParent, parentPart);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesEntryAction", state, astStateUsage,
          parentPart);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitAction", state, astStateUsage, parentPart);
      if(state.getIsAutomaton()) {
        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesTransitionTo", state, parentPart);
        createTransitionsForStateList(StatesResolveUtils.getStatesOfElement(state), state, parentPart);
      }
    }
  }
}
