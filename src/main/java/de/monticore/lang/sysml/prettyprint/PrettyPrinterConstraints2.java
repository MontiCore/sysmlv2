package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.constraints._ast.*;
import de.monticore.lang.sysml.advanced.constraints._visitor.ConstraintsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterConstraints2 implements ConstraintsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterConstraints2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTConstraintDefDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("constraint def " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTConstraintBody node) {
		printer.println("");
		if (node.isPresentConstraintMembers()) {
			printer.print("{");
			printer.indent();
		} else {
			printer.print(";");
		}
	}

	@Override
	public void endVisit(ASTConstraintBody node) {
		printer.println("");
		if (node.isPresentConstraintMembers()) {
			printer.unindent();
			printer.println("}");
		}
	}

	@Override
	public void visit(ASTConstraintUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("constraint ");
	}

	@Override
	public void visit(ASTBehaviorUsageMemberConstraintUsage node) {
		printer.println("");
		printer.print(node.getDefinitionMemberPrefix().toString() + " ");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("constraint ");
		} else {
			printer.print("ref constraint ");
		}
	}
}
