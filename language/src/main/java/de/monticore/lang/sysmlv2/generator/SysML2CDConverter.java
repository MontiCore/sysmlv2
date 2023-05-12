/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.generator.utils.ScalarValues;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PartResolveUtils;
import de.monticore.lang.sysmlv2.generator.visitor.Attributes2CDVisitor;
import de.monticore.lang.sysmlv2.generator.visitor.Parts2CDVisitor;
import de.monticore.lang.sysmlv2.generator.visitor.States2CDVisitor;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class SysML2CDConverter {
  ASTCDDefinition astcdDefinition;

  ASTCDPackage cdPackage;

  ASTCDCompilationUnit cdCompilationUnit;

  public SysML2CDData doConvert(ASTSysMLModel astSysMLModel, GlobalExtensionManagement glex, String packageName) {
    init(packageName);
    ScalarValues.init();
    // Main class, names equally to the Automaton

    // Phase 1: Work on parts
    Parts2CDVisitor phase1Visitor = new Parts2CDVisitor(glex, cdCompilationUnit, cdPackage, astcdDefinition);
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLParts(phase1Visitor);
    // Phase 2: Work on states
    States2CDVisitor states1Visitor = new States2CDVisitor(glex, cdCompilationUnit, cdPackage, astcdDefinition);
    traverser.add4SysMLStates(states1Visitor);
    // Phase 3: Work on attributes
    Attributes2CDVisitor attributeVisitor = new Attributes2CDVisitor(glex, cdCompilationUnit, cdPackage,
        astcdDefinition);
    traverser.add4SysMLParts(attributeVisitor);
    // we use the CD4Code language for the CD (and now switch to it)
    CD4CodeMill.init();

    traverser.handle(astSysMLModel);
    createMainClass(astSysMLModel);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(phase1Visitor.getCdCompilationUnit());
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
  }

  void createMainClass(ASTSysMLModel astSysMLModel) {
    ASTCDClass mainClass = CD4CodeMill.cDClassBuilder().setName("Main").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    cdPackage.addCDElement(mainClass);
    var partUsages = astSysMLModel.streamSysMLElements().filter(t -> t instanceof ASTSysMLPackage).flatMap(
        t -> PartResolveUtils.getSubPartsOfElement(t).stream()).collect(Collectors.toList());
    var flows = astSysMLModel.streamSysMLElements().filter(t -> t instanceof ASTSysMLPackage).flatMap(
        t -> ((ASTSysMLPackage) t).streamSysMLElements()).filter(t-> t instanceof ASTFlow).map(t -> (ASTFlow)t).collect(Collectors.toList());
    if(partUsages.isEmpty()) {
      Log.error("No Part Usages found that could be used for the simulation.");

    }
    CD4C.getInstance().addMethod(mainClass, "sysml2cd.MainMethod", partUsages, flows, cdPackage.getName());
  }

}
