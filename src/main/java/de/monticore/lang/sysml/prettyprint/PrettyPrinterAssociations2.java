/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTAssociationBlockBody;
import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTAssociationBlockDeclaration;
import de.monticore.lang.sysml.common.sysmlassociations._visitor.SysMLAssociationsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterAssociations2 implements SysMLAssociationsVisitor2 {
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
