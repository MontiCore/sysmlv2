/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;

import java.util.HashMap;
import java.util.Map;

public class SysML2CDConverter {

  public SysML2CDData doConvert(ASTSysMLModel astAutomaton, GlobalExtensionManagement glex) {

    // Phase 1: Work on states
    // Automata2CDStateVisitor phase1Visitor = new Automata2CDStateVisitor(glex);
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    //traverser.add4SysMLv2(phase1Visitor);

    // we use the CD4Code language for the CD (and now switch to it)
    CD4CodeMill.init();

    //traverser.handle(astAutomaton);

    // Phase 2: Work with transitions
    // Automata2CDUMLTransitionVisitor phase2Visitor = new Automata2CDUMLTransitionVisitor(phase1Visitor.getScClass(),
    //     phase1Visitor.getStateToClassMap(),
    //     phase1Visitor.getStateSuperClass());
    // traverser = AutomataMill.inheritanceTraverser();
    // traverser.add4Automata(phase2Visitor);
    //traverser.handle(astAutomaton);

    ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CDBasisMill.cDCompilationUnitBuilder();

    ASTCDClass automataClass = CDBasisMill.cDClassBuilder().setName("testClass")
        .setModifier(CDBasisMill.modifierBuilder().PUBLIC().build()).build();
    ASTCDClass stateSuperClass = new ASTCDClass();
    Map<String, ASTCDClass> stateToClassMap = new HashMap<>();
    // voila
   // return new SysML2CDData(cdCompilationUnitBuilder.build(), automataClass,
   //     stateSuperClass, stateToClassMap.values()
   // );
    return new SysML2CDData(new ASTCDCompilationUnit(),new ASTCDClass(),new ASTCDClass(), stateToClassMap.values());
  }

}
