package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlliterals._ast.ASTRealLiteralWithExponent;
import de.monticore.lang.sysml.basics.sysmlliterals._ast.ASTUnlimitedNaturalLiteralExpression;
import de.monticore.lang.sysml.basics.sysmlliterals._visitor.SysMLLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterLiterals2 implements SysMLLiteralsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterLiterals2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTUnlimitedNaturalLiteralExpression node) {
		printer.print("* ");
	}

	@Override
	public void endVisit(ASTRealLiteralWithExponent node) {
		printer.print(node.getDecimalDoublePointLiteral()+" ");
	}
}
