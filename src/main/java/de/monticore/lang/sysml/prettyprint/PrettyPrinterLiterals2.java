package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.literals._ast.ASTUnlimitedNaturalLiteralExpression;
import de.monticore.lang.sysml.basics.sysmldefault.literals._visitor.LiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterLiterals2 implements LiteralsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterLiterals2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTUnlimitedNaturalLiteralExpression node) {
		printer.print("* ");
	}
}
