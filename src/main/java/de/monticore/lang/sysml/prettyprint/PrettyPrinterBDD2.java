package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.sysmlbdd._ast.ASTBlockDeclaration;
import de.monticore.lang.sysml.sysmlbdd._ast.ASTReferencePropertyNonPortStructureUsageMember;
import de.monticore.lang.sysml.sysmlbdd._visitor.SysMLBDDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterBDD2 implements SysMLBDDVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterBDD2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTBlockDeclaration node) {
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("block ");
	}

	@Override
	public void visit(ASTReferencePropertyNonPortStructureUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("ref ");
	}

}
