package de.monticore.lang.sysml.prettyprint;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
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
		traverser.setDefinitionsHandler(new PrettyPrinterHandlerDefinitions2(printer, traverser));

		//Visitor
		traverser.add4AD(new PrettyPrinterAD2(printer));
		traverser.add4BDD(new PrettyPrinterBDD2(printer));
		traverser.add4Comments(new PrettyPrinterComments2(printer));
		traverser.add4Constraints(new PrettyPrinterConstraints2(printer));
		traverser.add4Definitions(new PrettyPrinterDefinitions2(printer));
		traverser.add4Expressions(new PrettyPrinterExpressions2(printer));
		traverser.add4IBD(new PrettyPrinterIBD2(printer));
		traverser.add4ImportsAndPackages(new PrettyPrinterImportsAndPackages2(printer));
		traverser.add4Literals(new PrettyPrinterLiterals2(printer));
		traverser.add4ParametricDiagram(new PrettyPrinterParametricDiagram2(printer));
		traverser.add4RequirementDiagram(new PrettyPrinterRequirementDiagram2(printer));
		traverser.add4SMD(new PrettyPrinterSMD2(printer));
		traverser.add4SuccessionsAndItemFlows(new PrettyPrinterSuccessionsAndItemFlows2(printer));
		traverser.add4Visibility(new PrettyPrinterVisibility2(printer));
		traverser.add4Names(new PrettyPrintNames2(printer));
		traverser.add4Associations(new PrettyPrinterAssociations2(printer));
		traverser.add4Classifiers(new PrettyPrinterClassifiers2(printer));
		traverser.add4SysMLCommonBasis(new PrettyPrinterSysMLCommonBasis2(printer));
		traverser.add4Usages(new PrettyPrintUsages2(printer));
		traverser.add4ValueTypes(new PrettyPrinterValueTypes2(printer));
		traverser.add4Ports(new PrettyPrinterPorts2(printer));
		traverser.add4SharedBasis(new PrettyPrinterSharedBasis2(printer));

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
