package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.smd._ast.*;
import de.monticore.lang.sysml.smd._visitor.SMDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSMD2 implements SMDVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSMD2(IndentPrinter print) {
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
		printer.println("");
		if (node.isPresentStateBodyPart()) {
			printer.print("{");
			printer.indent();
		} else {
			printer.print(";");
		}
	}

	@Override
	public void endVisit(ASTStateBody node) {
		if (node.isPresentStateBodyPart()) {
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
		printer.println("");
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
		printer.print("state");
	}
}
