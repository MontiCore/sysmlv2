/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.*;
import de.monticore.lang.sysml.advanced.sysmlconstraints._visitor.SysMLConstraintsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterConstraints2 implements SysMLConstraintsVisitor2 {
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
		if (node.isPresentConstraintMembers()) {
			printer.println("{");
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
