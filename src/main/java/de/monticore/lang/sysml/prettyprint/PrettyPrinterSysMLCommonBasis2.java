package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTFeatureDirection;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._visitor.SysMLCommonBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSysMLCommonBasis2 implements SysMLCommonBasisVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSysMLCommonBasis2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTFeatureDirection node) {
		if (node.isIn()) {
			printer.print("in ");
		} else if (node.isOut()) {
			printer.print("out ");
		} else if (node.isInout()) {
			printer.print("inout ");
		}
	}
}
