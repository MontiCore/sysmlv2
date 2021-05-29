/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.stm._ast.*;
import de.monticore.lang.sysml.stm._visitor.STMVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSTM2 implements STMVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSTM2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTStateDefDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("state def " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTStateBody node) {
		if (node.isPresentStateBodyPart()) {
			printer.println("{");
			printer.indent();
		} else {
			printer.print(";");
		}
	}

	@Override
	public void endVisit(ASTStateBody node) {
		if (node.isPresentStateBodyPart()) {
			printer.println("");
			printer.unindent();
			printer.print("}");
		}
	}

	@Override
	public void visit(ASTEntryActionKind node) {
		printer.print("entry ");
	}

	@Override
	public void visit(ASTDoActionKind node) {
		printer.print("do ");
	}

	@Override
	public void visit(ASTExitActionKind node) {
		printer.print("exit ");
	}

	@Override
	public void endVisit(ASTEntryTransitionMember node) {
		printer.print(";");
	}

	@Override
	public void endVisit(ASTTargetTransitionSuccessionMember node) {
		printer.print(";");
	}

	@Override
	public void endVisit(ASTTransitionStepMember node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTStateDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("state ");
	}

	@Override
	public void visit(ASTStateUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("state ");
	}
}
