package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.sysmlrd._ast.*;
import de.monticore.lang.sysml.sysmlrd._visitor.SysMLRDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterRequirementDiagram2 implements SysMLRDVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterRequirementDiagram2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTRequirementDefDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("requirement def ");
		if (node.isPresentReqId()) {
			printer.print("id " + node.getReqId().getNameForPrettyPrinting() + " ");
		}
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}

	@Override
	public void visit(ASTRequirementDefParameterList node) {
		printer.println("");
		printer.print("(");
		if (!(node.isPresentEmptyParameterMember() || node.isEmptyParameterMembers())) {
			printer.print(";");
		}
	}

	@Override
	public void endVisit(ASTRequirementDefParameterList node) {
		printer.print(") ");
	}

	@Override
	public void visit(ASTRequirementBody node) {
		if (node.isPresentRequirementMembers()) {
			printer.println("{");
			printer.indent();
		} else {
			printer.print(";");
		}
	}

	@Override
	public void endVisit(ASTRequirementBody node) {
		if (node.isPresentRequirementMembers()) {
			printer.unindent();
			printer.println("}");
		}
	}

	@Override
	public void visit(ASTRequirementConstraintKind node) {
		printer.println("");
		if (node.isAssumption()) {
			printer.print("assume ");
		} else if (node.isRequirement()) {
			printer.print("require ");
		}
	}

	@Override
	public void visit(ASTRequirementUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("requirement ");
	}

	@Override
	public void visit(ASTRequirementDeclaration node) {
		printer.println("");
		if (node.isPresentReqId()) {
			printer.print("id " + node.getReqId().getNameForPrettyPrinting() + " ");
		}
	}
}
