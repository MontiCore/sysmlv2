/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._ast.*;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.lang.sysml.ibd._visitor.IBDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterExpressions2 implements SysMLExpressionsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterExpressions2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTBaseExpression node) {
		if (node.isPresentExpression()) {
			printer.println("{");
			printer.indent();
		}
	}

	@Override
	public void endVisit(ASTBaseExpression node) {
		if (node.isPresentExpression()) {
			printer.println("");
			printer.unindent();
			printer.println("}");
		}
	}

	@Override
	public void visit(ASTClassExtentExpression node) {
		printer.println("");
	}

	@Override
	public void endVisit(ASTClassExtentExpression node) {
		printer.print(". allInstances ( )");
	}

	@Override
	public void visit(ASTNullExpression node) {
		printer.println("");
		printer.print("null");
	}

	@Override
	public void visit(ASTNamedExpressionMember node) {
		printer.println("");
		printer.print(node.getMemberName().getNameForPrettyPrinting() + " => ");
	}

	@Override
	public void visit(ASTBodyParameterMember node) {
		printer.println("");
		printer.print(node.getMemberName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTQueryHeadExpression node) {
		printer.println("");
		printer.print("./");
	}
}
