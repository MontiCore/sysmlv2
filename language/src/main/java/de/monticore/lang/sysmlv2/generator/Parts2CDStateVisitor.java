/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.Splitters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parts2CDStateVisitor implements SysMLPartsVisitor2 {

  public final static String ERROR_CODE = "0xDC012";

  protected ASTPartDef astAutomaton;

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDPackage cdPackage;

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

  public Parts2CDStateVisitor(GlobalExtensionManagement glex) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
  }

  @Override
  public void visit(ASTPartDef astPartDef) {
    this.astAutomaton = astPartDef;

    // Add a CDDefinition
    ASTCDDefinition astcdDefinition = CDBasisMill.cDDefinitionBuilder().setName(astPartDef.getName())
        .setModifier(UMLModifierMill.modifierBuilder().build()).build();
    if(cdPackage == null) {
      cdPackage = CDBasisMill.cDPackageBuilder()
          .setMCQualifiedName(CDBasisMill.mCQualifiedNameBuilder()
              .setPartsList(Arrays.asList(astPartDef.getName().toLowerCase()))
              .build())
          .build();
    }
    astcdDefinition.addCDElement(cdPackage);

    if(cdCompilationUnit == null) {
      ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CDBasisMill.cDCompilationUnitBuilder();
      cdCompilationUnitBuilder.setCDDefinition(astcdDefinition);

      cdCompilationUnit = cdCompilationUnitBuilder.build();
    }

    // Main class, names equally to the Automaton
    partDefClass = CDBasisMill.cDClassBuilder().setName(astPartDef.getName())
        .setModifier(CDBasisMill.modifierBuilder().PUBLIC().build()).build();
    cdPackage.addCDElement(partDefClass);

    stateToClassMap.put(astPartDef.getName(), partDefClass);
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
  protected ASTMCQualifiedType qualifiedType(String qname) {
    return qualifiedType(Splitters.DOT.splitToList(qname));
  }

  protected ASTMCQualifiedType qualifiedType(List<String> partsList) {
    return CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(partsList).build()).build();
  }
}
