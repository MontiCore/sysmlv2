/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml.SysMLMill;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinter2 {
	private final SysMLTraverser traverser;
	private final IndentPrinter printer;

	public PrettyPrinter2() {
		printer = new IndentPrinter();
		traverser = SysMLMill.traverser();

		//Handler
		traverser.setADHandler(new PrettyPrinterHandlerAD2(printer,traverser));
		traverser.setBDDHandler(new PrettyPrinterHandlerBDD2(printer,traverser));
		traverser.setRequirementDiagramHandler(new PrettyPrinterHandlerRequirementDiagram2(printer,traverser));
		traverser.setSTMHandler(new PrettyPrinterHandlerSMD2(printer, traverser));
		traverser.setSysMLConstraintsHandler(new PrettyPrinterHandlerConstraints2(printer, traverser));
		traverser.setSysMLItemFlowsHandler(new PrettyPrinterHandlerSysMLItemFlows2(printer,traverser));
		traverser.setIBDHandler(new PrettyPrinterHandlerIBD2(printer, traverser));
		traverser.setSysMLExpressionsHandler(new PrettyPrinterHandlerExpressions2(printer,traverser));
		traverser.setSysMLImportsAndPackagesHandler(new PrettyPrinterHandlerImportsAndPackages2(printer, traverser));
		traverser.setSysMLAssociationsHandler(new PrettyPrinterHandlerAssociations2(printer, traverser));
		traverser.setSysMLClassifiersHandler(new PrettyPrinterHandlerClassifiers2(printer, traverser));
		traverser.setSysMLUsagesHandler(new PrettyPrinterHandlerUsages2(printer, traverser));
		traverser.setSysMLPortsHandler(new PrettyPrinterHandlerPorts2(printer, traverser));
		traverser.setSysMLDefinitionsHandler(new PrettyPrinterHandlerDefinitions2(printer, traverser));

		//Visitor
		traverser.add4AD(new PrettyPrinterAD2(printer));
		traverser.add4BDD(new PrettyPrinterBDD2(printer));
		traverser.add4SysMLComments(new PrettyPrinterComments2(printer));
		traverser.add4SysMLConstraints(new PrettyPrinterConstraints2(printer));
		traverser.add4SysMLDefinitions(new PrettyPrinterDefinitions2(printer));
		traverser.add4SysMLExpressions(new PrettyPrinterExpressions2(printer));
		traverser.add4IBD(new PrettyPrinterIBD2(printer));
		traverser.add4SysMLImportsAndPackages(new PrettyPrinterImportsAndPackages2(printer));
		traverser.add4SysMLLiterals(new PrettyPrinterLiterals2(printer));
		traverser.add4ParametricDiagram(new PrettyPrinterParametricDiagram2(printer));
		traverser.add4RequirementDiagram(new PrettyPrinterRequirementDiagram2(printer));
		traverser.add4STM(new PrettyPrinterSTM2(printer));
		traverser.add4SysMLItemFlows(new PrettyPrinterSysMLItemFlows2(printer));
		traverser.add4SysMLVisibility(new PrettyPrinterVisibility2(printer));
		traverser.add4SysMLNames(new PrettyPrintNames2(printer));
		traverser.add4SysMLAssociations(new PrettyPrinterAssociations2(printer));
		traverser.add4SysMLClassifiers(new PrettyPrinterClassifiers2(printer));
		traverser.add4SysMLCommonBasis(new PrettyPrinterSysMLCommonBasis2(printer));
		traverser.add4SysMLUsages(new PrettyPrintUsages2(printer));
		traverser.add4SysMLValueTypes(new PrettyPrinterValueTypes2(printer));
		traverser.add4SysMLPorts(new PrettyPrinterPorts2(printer));
		traverser.add4SysMLShared(new PrettyPrinterSharedBasis2(printer));

		//Monticore
		traverser.add4MCCommonLiterals(new MCCommonLiteralsPrettyPrinter(printer));
		traverser.setMCCommonLiteralsHandler(new MCCommonLiteralsPrettyPrinter(printer));
		traverser.add4CommonExpressions(new CommonExpressionsPrettyPrinter(printer));
		traverser.setCommonExpressionsHandler(new CommonExpressionsPrettyPrinter(printer));
		traverser.add4ExpressionsBasis(new ExpressionsBasisPrettyPrinter(printer));
		traverser.setExpressionsBasisHandler(new ExpressionsBasisPrettyPrinter(printer));
	}

	public String prettyPrint(ASTUnit ast) {
		printer.clearBuffer();
		ast.accept(traverser);
		return printer.getContent();
	}
}
