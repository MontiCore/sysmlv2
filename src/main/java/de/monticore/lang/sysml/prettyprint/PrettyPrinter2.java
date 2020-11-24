package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml.SysMLMill;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinter2 {
	private final SysMLTraverser traverser;
	private final IndentPrinter printer;

	public PrettyPrinter2() {
		printer = new IndentPrinter();
		traverser = SysMLMill.traverser();

		//Visitor
		traverser.setADVisitor(new PrettyPrinterAD2(printer));
		traverser.setBDDVisitor(new PrettyPrinterBDD2(printer));
		traverser.setCommentsVisitor(new PrettyPrinterComments2(printer));
		traverser.setConstraintsVisitor(new PrettyPrinterConstraints2(printer));
		traverser.setDefinitionsVisitor(new PrettyPrinterDefinitions2(printer));
		traverser.setExpressionsVisitor(new PrettyPrinterExpressions2(printer));
		traverser.setIBDVisitor(new PrettyPrinterIBD2(printer));
		traverser.setImportsAndPackagesVisitor(new PrettyPrinterImportsAndPackages2(printer));
		traverser.setLiteralsVisitor(new PrettyPrinterLiterals2(printer));
		traverser.setParametricDiagramVisitor(new PrettyPrinterParametricDiagram2(printer));
		traverser.setRequirementDiagramVisitor(new PrettyPrinterRequirementDiagram2(printer));
		traverser.setSMDVisitor(new PrettyPrinterSMD2(printer));
		traverser.setSuccessionsAndItemFlowsVisitor(new PrettyPrinterSuccessionsAndItemFlows2(printer));
		traverser.setVisibilityVisitor(new PrettyPrinterVisibility2(printer));
		traverser.setNamesVisitor(new PrettyPrintNames2(printer));
		traverser.setAssociationsVisitor(new PrettyPrinterAssociations2(printer));
		traverser.setClassifiersVisitor(new PrettyPrinterClassifiers2(printer));
		traverser.setSysMLCommonBasisVisitor(new PrettyPrinterSysMLCommonBasis2(printer));
		traverser.setUsagesVisitor(new PrettyPrintUsages2(printer));
		traverser.setValueTypesVisitor(new PrettyPrinterValueTypes2(printer));
		traverser.setPortsVisitor(new PrettyPrinterPorts2(printer));

		//Handler
		traverser.setADHandler(new PrettyPrinterHandlerAD2(printer,traverser));
		traverser.setBDDHandler(new PrettyPrinterHandlerBDD2(printer,traverser));
		traverser.setRequirementDiagramHandler(new PrettyPrinterHandlerRequirementDiagram2(printer,traverser));
		traverser.setSMDHandler(new PrettyPrinterHandlerSMD2(printer, traverser));
		traverser.setConstraintsHandler(new PrettyPrinterHandlerConstraints2(printer, traverser));
		traverser.setSuccessionsAndItemFlowsHandler(new PrettyPrinterHandlerSuccessionsAndItemFlows2(printer,traverser));
		traverser.setIBDHandler(new PrettyPrinterHandlerIBD2(printer, traverser));
		traverser.setExpressionsHandler(new PrettyPrinterHandlerExpressions2(printer,traverser));
		traverser.setImportsAndPackagesHandler(new PrettyPrinterHandlerImportsAndPackages2(printer, traverser));
		traverser.setAssociationsHandler(new PrettyPrinterHandlerAssociations2(printer, traverser));
		traverser.setClassifiersHandler(new PrettyPrinterHandlerClassifiers2(printer, traverser));
		traverser.setUsagesHandler(new PrettyPrinterHandlerUsages2(printer, traverser));
		traverser.setPortsHandler(new PrettyPrinterHandlerPorts2(printer, traverser));
	}

	public String prettyPrint(ASTUnit ast) {
		printer.clearBuffer();
		ast.accept(traverser);
		return printer.getContent();
	}
}
