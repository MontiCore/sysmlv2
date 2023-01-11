/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;

public class SysML2CDConverter {

  public SysML2CDData doConvert(ASTSysMLModel astAutomaton, GlobalExtensionManagement glex) {

    // Phase 1: Work on parts
    Parts2CDStateVisitor phase1Visitor = new Parts2CDStateVisitor(glex);
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLParts(phase1Visitor);

    // we use the CD4Code language for the CD (and now switch to it)
    CD4CodeMill.init();

    traverser.handle(astAutomaton);

    // voila
    return new SysML2CDData(phase1Visitor.getCdCompilationUnit(), phase1Visitor.getScClass(),
        phase1Visitor.getStateToClassMap().values());
  }

}
