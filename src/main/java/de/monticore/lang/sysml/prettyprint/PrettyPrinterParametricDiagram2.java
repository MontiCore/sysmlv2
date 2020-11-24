package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.parametricdiagram._ast.*;
import de.monticore.lang.sysml.parametricdiagram._visitor.ParametricDiagramVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterParametricDiagram2 implements ParametricDiagramVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterParametricDiagram2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTIndividualDefDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("individual def ");
	}

	@Override
	public void visit(ASTIndividualUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("individual ");
	}

	@Override
	public void visit(ASTTimeSliceUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("timeslice ");
	}

	@Override
	public void visit(ASTSnapshotUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("snapshot ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberIndividualUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("individual ");
		} else {
			printer.print("ref individual ");
		}
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberTimeSliceUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("timeslice ");
		} else {
			printer.print("ref timeslice ");
		}
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberSnapshotUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("snapshot ");
		} else {
			printer.print("ref snapshot ");
		}
	}
}
