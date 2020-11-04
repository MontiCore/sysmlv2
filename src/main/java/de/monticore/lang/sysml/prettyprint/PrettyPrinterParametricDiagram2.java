package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.parametricdiagram._ast.*;
import de.monticore.lang.sysml.parametricdiagram._visitor.ParametricDiagramVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterParametricDiagram2 extends IndentPrinter implements ParametricDiagramVisitor2 {
	@Override
	public void visit(ASTIndividualDefDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("individual def ");
	}

	@Override
	public void visit(ASTIndividualUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("individual ");
	}

	@Override
	public void visit(ASTTimeSliceUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("timeslice ");
	}

	@Override
	public void visit(ASTSnapshotUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("snapshot ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberIndividualUsage node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("individual ");
		} else {
			print("ref individual ");
		}
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberTimeSliceUsage node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("timeslice ");
		} else {
			print("ref timeslice ");
		}
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberSnapshotUsage node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("snapshot ");
		} else {
			print("ref snapshot ");
		}
	}
}
