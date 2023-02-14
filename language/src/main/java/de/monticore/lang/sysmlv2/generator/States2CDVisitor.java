/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

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
  }

  @Override
  public void visit(ASTStateUsage astStateUsage) {
    cdPackage = generatorUtils.initCdPackage(astStateUsage, astcdDefinition, basePackage.getName());
    if(astStateUsage.getIsAutomaton()) {
      //create class
      stateUsageClass = CD4CodeMill.cDClassBuilder()
          .setName(astStateUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      //attributes
      ASTMCQualifiedType qualifiedType = CD4CodeMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(
              List.of(astStateUsage.getName() + "Enum")).build()).build();
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      attributeList.add(CD4CodeMill.cDAttributeBuilder().setMCType(qualifiedType).setName(
          "currentState").setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build());
      ASTCDAttribute parentAttribute = createParentAttribute(astStateUsage);
      attributeList.add(parentAttribute);
      attributeList.addAll(attributeUtils.createAttributes(astStateUsage));
      stateUsageClass.setCDAttributeList(attributeList);

      cdPackage.addCDElement(stateUsageClass);
      //create state enum
      var stateList = astStateUsage.streamSysMLElements().filter(t -> t instanceof ASTStateUsage).map(
          t -> (ASTStateUsage) t).collect(
          Collectors.toList());
      cdPackage.addCDElement(createEnum(astStateUsage, stateList));
      //add methods
      componentUtils.setPortLists((ASTSysMLElement) astStateUsage.getEnclosingScope().getAstNode());
      var inputPortsParent = componentUtils.inputPortList;
      var outputPortsParents = componentUtils.outputPortList;
      generatorUtils.addMethods(stateUsageClass, attributeList, true, true);
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitMethod", stateList,
          astStateUsage.getName() + "Enum");
      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesCompute", stateList);
      cd4C.addConstructor(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesConstructor", astStateUsage,
          astStateUsage.getName() + "Enum", parentAttribute.printType());
      for (ASTStateUsage state :
          stateList) {

        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesTransition", state, astStateUsage,
            astStateUsage.getName() + "Enum", inputPortsParent, outputPortsParents);
        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesEntryAction", state, astStateUsage);
        cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesExitAction", state, astStateUsage);

      }
    }
  }

  ASTCDEnum createEnum(ASTStateUsage astStateUsage, List<ASTStateUsage> stateList) {
    List<ASTCDEnumConstant> enumConstantList = stateList.stream().map(
        state -> CD4CodeMill.cDEnumConstantBuilder().setName(state.getName()).build()).collect(
        Collectors.toList());

    return CD4CodeMill.cDEnumBuilder().setName(astStateUsage.getName() + "Enum").setCDEnumConstantsList(
        enumConstantList).setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
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

}
