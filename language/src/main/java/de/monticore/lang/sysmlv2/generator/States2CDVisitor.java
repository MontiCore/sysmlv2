/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;
import de.monticore.lang.sysmlv2.generator.timesync.PartUtils;
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

  protected final GlobalExtensionManagement glex;

  public States2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                          ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
  }

  @Override
  public void visit(ASTStateUsage astStateUsage) {
    cdPackage = generatorUtils.initCdPackage(astStateUsage, astcdDefinition, basePackage.getName());
    if(astStateUsage.getIsAutomaton()) {
      var stateList = astStateUsage.streamSysMLElements().filter(t -> t instanceof ASTStateUsage).map(
          t -> (ASTStateUsage) t).collect(
          Collectors.toList());

      stateUsageClass = CD4CodeMill.cDClassBuilder()
          .setName(astStateUsage.getName())
          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();

      ASTMCQualifiedType qualifiedType = CD4CodeMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(
              List.of(astStateUsage.getName() + "Enum")).build()).build();
      var attribute = CD4CodeMill.cDAttributeBuilder().setMCType(qualifiedType).setName(
          "currentState").setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();

      stateUsageClass.setCDAttributeList(List.of(attribute));
      cdPackage.addCDElement(stateUsageClass);

      var enumConstantList = stateList.stream().map(
          state -> CD4CodeMill.cDEnumConstantBuilder().setName(state.getName().toUpperCase()).build()).collect(
          Collectors.toList());

      var anEnum = CD4CodeMill.cDEnumBuilder().setName(astStateUsage.getName() + "Enum").setCDEnumConstantsList(
          enumConstantList).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      cdPackage.addCDElement(anEnum);

      cd4C.addMethod(stateUsageClass, "sysml2cd.Automaton.AutomatonStatesCompute", stateList);
    }
  }

}
