package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.bdd._ast.ASTAssociationEndMemberReferenceProperty;
import de.monticore.lang.sysml.bdd._ast.ASTBlockDeclaration;
import de.monticore.lang.sysml.bdd._ast.ASTReferencePropertyNonPortStructureUsageMember;
import de.monticore.lang.sysml.bdd._visitor.BDDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterBDD2 implements BDDVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterBDD2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTBlockDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("block " + node.getName());
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
