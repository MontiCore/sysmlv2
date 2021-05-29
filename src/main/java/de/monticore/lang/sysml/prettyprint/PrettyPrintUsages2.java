/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlusages._ast.*;
import de.monticore.lang.sysml.common.sysmlusages._visitor.SysMLUsagesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrintUsages2 implements SysMLUsagesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrintUsages2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTValuePart node) {
		printer.print("= ");
	}

	@Override
	public void visit(ASTTypedByKeyword node) {
		printer.print("typed by ");
	}

	@Override
	public void endVisit(ASTMultiplicityPart node) {
		if (node.isOrdered()) {
			printer.print("ordered ");
		}
		if (node.isNonunique()) {
			printer.print("nonunique ");
		}
	}

	@Override
	public void visit(ASTMultiplicity node) {
		printer.print("[");
		if(node.isPresentFromToDigitsDotDot()){
			printer.print(node.getFromToDigitsDotDot());
		}
	}

	@Override
	public void endVisit(ASTMultiplicity node) {
		printer.print("] ");
	}

	@Override
	public void visit(ASTSubsetsKeyword node) {
		printer.print("subsets ");
	}

	@Override
	public void visit(ASTRedefinesKeyword node) {
		printer.print("redefines ");
	}

	@Override
	public void visit(ASTUnlimitedNaturalLiteralMember node) {
		if (node.isIsUnlimited()) {
			printer.print("* ");
		}
	}
}
