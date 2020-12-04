package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ad._ast.*;
import de.monticore.lang.sysml.ad._visitor.ADVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterAD2 implements ADVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterAD2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTActivityDeclaration node) {
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("activity " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTActivityBodyStd node) {
		if (node.getActivityBodyItemList().size()==0) {
			printer.print(";");
		} else {
			printer.println("{");
			printer.indent();
		}
	}

	@Override
	public void endVisit(ASTActivityBodyStd node) {
		if (node.getActivityBodyItemList().size()>0) {
			printer.unindent();
			printer.println("");
			printer.println("}");
		}
	}

	@Override
	public void visit(ASTTargetSuccessionMember node) {
		printer.println("");
	}

	@Override
	public void endVisit(ASTTargetSuccessionMember node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTGuardedSuccessionMember node) {
		printer.println("");
	}

	@Override
	public void endVisit(ASTGuardedSuccessionMember node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTAssociationEndMemberActionUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
		printer.print("end ");
	}

	@Override
	public void visit(ASTActionDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("action");
	}

	@Override
	public void visit(ASTActionUsagePackagedUsageMember node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("action ");
	}

	@Override
	public void visit(ASTActionUsageNodeDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
	}

	@Override
	public void visit(ASTPerformActionNodeDeclaration node) {
		printer.println("");
		if (node.isIsComposite()) {
			printer.print("perform ");
		}
		if (node.isPresentSysMLNameAndTypePart()) {
			printer.print(node.getSysMLNameAndTypePart().getName() + " as ");
		}
	}

	@Override
	public void visit(ASTMergeNode node) {
		printer.print("merge ");
	}

	@Override
	public void endVisit(ASTMergeNode node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTDecisionNode node) {
		printer.print("decide ");
	}

	@Override
	public void endVisit(ASTDecisionNode node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTJoinNode node) {
		printer.println("");
		printer.print("join ");
	}

	@Override
	public void endVisit(ASTJoinNode node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTForkNode node) {
		printer.print("fork ");
	}

	@Override
	public void endVisit(ASTForkNode node) {
		printer.print(";");
	}
}
