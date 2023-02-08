/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

import java.util.List;

public class Attributes2CDVisitor implements SysMLPartsVisitor2 {

  public final static String ERROR_CODE = "0xDC012";

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

  InterfaceUtils interfaceUtils;

  AttributeUtils attributeUtils;

  protected final GlobalExtensionManagement glex;

  public Attributes2CDVisitor(GlobalExtensionManagement glex, ASTCDCompilationUnit cdCompilationUnit,
                              ASTCDPackage basePackage, ASTCDDefinition astcdDefinition) {
    this.cd4C = CD4C.getInstance();
    this.glex = glex;
    this.cdCompilationUnit = cdCompilationUnit;
    this.basePackage = basePackage;
    this.astcdDefinition = astcdDefinition;
    this.generatorUtils = new GeneratorUtils();
    this.interfaceUtils = new InterfaceUtils();
    this.attributeUtils = new AttributeUtils();
  }

  @Override
  public void visit(ASTAttributeDef astAttributeDef) {
    cdPackage = generatorUtils.initCdPackage(astAttributeDef, astcdDefinition, basePackage.getName());
    // Step 1: Create Interface for the Part Def to support multiple inheritance
    ASTCDInterfaceUsage interfaceUsage = interfaceUtils.createInterfaceUsage(List.of(astAttributeDef));
    cdPackage.addCDElement(interfaceUtils.createInterface(astAttributeDef));
    //Step 2 Create class
    partDefClass = CD4CodeMill.cDClassBuilder().setCDInterfaceUsage(interfaceUsage)
        .setName(astAttributeDef.getName())
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setCDInterfaceUsage(interfaceUsage).build();
    List<ASTCDAttribute> liste = attributeUtils.createAttributes(astAttributeDef);
    partDefClass.setCDAttributeList(liste);
    generatorUtils.addMethods(partDefClass, liste, true, true);
    cdPackage.addCDElement(partDefClass);
  }

}
