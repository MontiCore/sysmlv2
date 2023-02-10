/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.umlmodifier.UMLModifierMill;

import java.util.List;

public class SysML2CDConverter {
  ASTCDDefinition astcdDefinition;

  ASTCDPackage cdPackage;

  ASTCDCompilationUnit cdCompilationUnit;

  public SysML2CDData doConvert(ASTSysMLModel astSysMLModel, GlobalExtensionManagement glex, String packageName) {
    init(packageName);
    // Main class, names equally to the Automaton

    // Phase 1: Work on parts
    Parts2CDVisitor phase1Visitor = new Parts2CDVisitor(glex, cdCompilationUnit, cdPackage, astcdDefinition);
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLParts(phase1Visitor);

    States2CDVisitor states1Visitor = new States2CDVisitor(glex, cdCompilationUnit, cdPackage, astcdDefinition);
    traverser.add4SysMLStates(states1Visitor);

    Attributes2CDVisitor attributeVisitor = new Attributes2CDVisitor(glex, cdCompilationUnit, cdPackage, astcdDefinition);
    traverser.add4SysMLParts(attributeVisitor);

    // we use the CD4Code language for the CD (and now switch to it)
    CD4CodeMill.init();

    traverser.handle(astSysMLModel);

    // voila
    return new SysML2CDData(phase1Visitor.getCdCompilationUnit(), phase1Visitor.getScClass());
  }

  void init(String packageName) {

    // Add a CDDefinition
    astcdDefinition = CD4CodeMill.cDDefinitionBuilder().setName(packageName)
        .setModifier(UMLModifierMill.modifierBuilder().build()).build();
    cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder()
            .setPartsList(List.of(packageName.toLowerCase()))
            .build())
        .build();

    astcdDefinition.addCDElement(cdPackage);
    ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CD4CodeMill.cDCompilationUnitBuilder();
    cdCompilationUnitBuilder.setCDDefinition(astcdDefinition);
    cdCompilationUnit = cdCompilationUnitBuilder.build();
    CD4CodeMill.init();
    ASTCDClass mainClass = CD4CodeMill.cDClassBuilder().setName("Main").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    cdPackage.addCDElement(mainClass);
  }

}
