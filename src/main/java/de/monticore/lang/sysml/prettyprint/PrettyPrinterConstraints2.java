package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.constraints._ast.*;
import de.monticore.lang.sysml.advanced.constraints._visitor.ConstraintsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterConstraints2 extends IndentPrinter implements ConstraintsVisitor2 {
	@Override
	public void visit(ASTConstraintDefDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("constraint def " + node.getSysMLName().getNameForPrettyPrinting() + " ");
	}

	@Override
	public void visit(ASTConstraintBody node) {
		println("");
		if (node.isPresentConstraintMembers()) {
			print("{");
			indent();
		} else {
			print(";");
		}
	}

	@Override
	public void endVisit(ASTConstraintBody node) {
		println("");
		if (node.isPresentConstraintMembers()) {
			unindent();
			println("}");
		}
	}

	@Override
	public void visit(ASTConstraintUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("constraint ");
	}

	@Override
	public void visit(ASTBehaviorUsageMemberConstraintUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " ");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("constraint ");
		} else {
			print("ref constraint ");
		}
	}

	@Override
	public void visit(ASTBehaviorUsageMemberAssertConstraintUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " ");
		print("assert ");
	}

	@Override
	public void visit(ASTAssertConstraintUsage node) {
		println("");
		if (!node.isEmptySubsets()) {
			if (node.isPresentSymbol()) {
				if (node.isPresentSysMLName()) {
					print(node.getSysMLName().getNameForPrettyPrinting() + " ");
				}
				if (node.isPresentTypePart()) {
					print(node.getTypePart().toString() + " ");
				}
				print("as ");
			}
		} else {
			print("constraint ");
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
		}
	}
}
