package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.associations._ast.ASTAssociationBlockBody;
import de.monticore.lang.sysml.basics.associations._ast.ASTAssociationBlockDeclaration;
import de.monticore.lang.sysml.basics.associations._visitor.AssociationsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterAssociations2 implements AssociationsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterAssociations2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTAssociationBlockDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("assoc block ");
	}

	@Override
	public void visit(ASTAssociationBlockBody node) {
		if (node.isEmptyImportUnits() && node.isEmptyAssociationUsageMembers() && node.isEmptyNestedDefinitionMembers()) {
			printer.print(";");
		} else {
			printer.println("{");
			printer.indent();
		}
	}

	@Override
	public void endVisit(ASTAssociationBlockBody node) {
		if (!(node.isEmptyImportUnits() && node.isEmptyAssociationUsageMembers() && node.isEmptyNestedDefinitionMembers())) {
			printer.println("");
			printer.unindent();
			printer.println("}");
		}
	}
}
