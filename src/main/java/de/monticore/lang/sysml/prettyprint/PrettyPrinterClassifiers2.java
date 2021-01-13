package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTSpecializesKeyword;
import de.monticore.lang.sysml.common.sysmlclassifiers._visitor.SysMLClassifiersVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterClassifiers2 implements SysMLClassifiersVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterClassifiers2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTSpecializesKeyword node) {
		printer.print("specializes ");
	}

	@Override
	public void visit(ASTClassifierDeclarationCompletionStd node) {
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}
}
