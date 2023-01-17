/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.umlmodifier.UMLModifierMill;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SysML2CDConverter {
  ASTCDDefinition astcdDefinition;
  ASTCDPackage cdPackage;
  ASTCDCompilationUnit cdCompilationUnit;
  public SysML2CDData doConvert(ASTSysMLModel astSysMLModel, GlobalExtensionManagement glex) {
    init(astSysMLModel);
    // Main class, names equally to the Automaton

    // Phase 1: Work on parts
    Parts2CDStateVisitor phase1Visitor = new Parts2CDStateVisitor(glex, cdCompilationUnit, cdPackage);
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLParts(phase1Visitor);

    // we use the CD4Code language for the CD (and now switch to it)
    CD4CodeMill.init();

    traverser.handle(astSysMLModel);

    // voila
    return new SysML2CDData(phase1Visitor.getCdCompilationUnit(), phase1Visitor.getScClass(),
        phase1Visitor.getStateToClassMap().values());
  }


  void init(ASTSysMLModel astSysMLModel){

    String name;
    var ha = astSysMLModel.getSysMLElementList().stream().filter(t -> t instanceof ASTSysMLPackage).map(t -> ((ASTSysMLPackage) t).getName()).collect(
        Collectors.toList());
    if(!ha.isEmpty()){
      name = ha.get(0);
    }else {
      name = "SysMLv2Generation";
    }
    // Add a CDDefinition
    astcdDefinition = CD4CodeMill.cDDefinitionBuilder().setName(name)
        .setModifier(UMLModifierMill.modifierBuilder().build()).build();
    cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder()
            .setPartsList(Arrays.asList(name.toLowerCase()))
            .build())
        .build();

    astcdDefinition.addCDElement(cdPackage);
    ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CD4CodeMill.cDCompilationUnitBuilder();
    cdCompilationUnitBuilder.setCDDefinition(astcdDefinition);
    cdCompilationUnit = cdCompilationUnitBuilder.build();
    CD4CodeMill.init();
  }

}
