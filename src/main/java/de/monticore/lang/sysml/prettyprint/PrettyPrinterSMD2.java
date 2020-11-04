package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.smd._ast.*;
import de.monticore.lang.sysml.smd._visitor.SMDVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSMD2 extends IndentPrinter implements SMDVisitor2 {
	@Override
	public void visit(ASTStateDefDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("state def " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void endVisit(ASTStateDefDeclaration node) {

	}

	@Override
	public void visit(ASTStateBody node) {
		println("");
		if (node.isPresentStateBodyPart()) {
			print("{");
			indent();
		} else {
			print(";");
		}
	}

	@Override
	public void endVisit(ASTStateBody node) {
		if (node.isPresentStateBodyPart()) {
			unindent();
			print("}");
		}
	}

	@Override
	public void visit(ASTStateActionUsage node) {
		println("");

	}

	@Override
	public void endVisit(ASTStateActionUsage node) {
		;
		if (!node.isPresentPerformedActionUsage()) {
			print(";");
		}
		print(node.getActivityBody().toString());
	}

	@Override
	public void visit(ASTEntryActionKind node) {
		println("");
		print("entry ");
	}

	@Override
	public void visit(ASTDoActionKind node) {
		println("");
		print("do ");
	}

	@Override
	public void visit(ASTExitActionKind node) {
		println("");
		print("exit ");
	}

	@Override
	public void visit(ASTStateMember node) {
		println("");
		print(node.getDefinitionMemberPrefix() + " ");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isIsComposite()) {
			print("state ");
		} else {
			print("ref state ");
		}
	}

	@Override
	public void endVisit(ASTEntryTransitionMember node) {
		print(";");
	}

	@Override
	public void endVisit(ASTTargetTransitionSuccessionMember node) {
		println("");
		print(";");
	}

	@Override
	public void endVisit(ASTTransitionStepMember node) {
		println("");
		print(";");
	}

	@Override
	public void visit(ASTStateDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("state ");
	}

	@Override
	public void visit(ASTExhibitStateUsage node) {
		println("");
		if (node.isPresentSubset()) {
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting() + " ");
				if (node.isPresentTypePart()) {
					print(node.getTypePart().toString() + " ");
				}
				print("as ");
			}
		} else {
			print("state ");
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
		}
	}

	@Override
	public void visit(ASTStateUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("state");
	}

	@Override
	public void visit(ASTBehaviorUsageMemberStateUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " ");
		if (node.isPresentIsComposite()) {
			print("state ");
		} else {
			print("ref state ");
		}
	}

	@Override
	public void visit(ASTBehaviorUsageMemberExhibitStateUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " exhibit ");
	}
}
