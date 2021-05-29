/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ibd._ast.ASTAssociationEndMemberPartProperty;
import de.monticore.lang.sysml.ibd._ast.ASTPartDeclaration;
import de.monticore.lang.sysml.ibd._ast.ASTPartProperty;
import de.monticore.lang.sysml.ibd._ast.ASTPartPropertyUsageMember;
import de.monticore.lang.sysml.ibd._visitor.IBDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterIBD2 implements IBDVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterIBD2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTPartDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("part ");
	}

	@Override
	public void visit(ASTPartPropertyUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
	}

	@Override
	public void visit(ASTAssociationEndMemberPartProperty node) {
		printer.println("");
		printer.print(node.getDefinitionMemberPrefix().toString() + " ");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end");
	}

	@Override
	public void visit(ASTPartProperty node) {
		printer.println("");
		printer.print("part ");
	}
}
