package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.requirementdiagram._ast.*;
import de.monticore.lang.sysml.requirementdiagram._visitor.RequirementDiagramVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterRequirementDiagram2 extends IndentPrinter implements RequirementDiagramVisitor2 {
	@Override
	public void visit(ASTRequirementDefDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("requirement def ");
		if (node.isPresentReqId()) {
			print("id " + node.getReqId().getNameForPrettyPrinting() + " ");
		}
		print(node.getSysMLName().getNameForPrettyPrinting());
	}

	@Override
	public void visit(ASTRequirementDefParameterList node) {
		println("");
		print("(");
		if (!(node.isPresentEmptyParameterMember() || node.isEmptyParameterMembers())) {
			print(";");
		}
	}

	@Override
	public void endVisit(ASTRequirementDefParameterList node) {
		print(")");
	}

	@Override
	public void visit(ASTRequirementBody node) {
		println("");
		if (node.isPresentRequirementMembers()) {
			print("{");
			indent();
		} else {
			print(";");
		}
	}

	@Override
	public void endVisit(ASTRequirementBody node) {
		if (node.isPresentRequirementMembers()) {
			unindent();
			println("}");
		}
	}

	@Override
	public void visit(ASTRequirementConstraintUsage node) {
		println("");
		if (node.isPresentSubset()) {
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting());
			}
			if (node.isPresentTypePart()) {
				print(node.getTypePart().toString());
			}
			if (node.isPresentSymbol()) {
				print("as");
			}
		} else {
			print("constraint ");
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
		}
	}

	@Override
	public void visit(ASTRequirementConstraintKind node) {
		println("");
		if (node.isAssumption()) {
			print("assume ");
		} else if (node.isRequirement()) {
			print("require ");
		}
	}

	@Override
	public void visit(ASTRequirementUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("requirement ");
	}

	@Override
	public void visit(ASTBehaviorUsageMemberRequirementUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " ");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("requirement ");
		} else {
			print("ref requirement ");
		}
	}

	@Override
	public void visit(ASTBehaviorUsageMemberSatisfyRequirementUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " satisfy ");
	}

	@Override
	public void visit(ASTRequirementDeclaration node) {
		println("");
		if (node.isPresentReqId()) {
			print("id " + node.getReqId().getNameForPrettyPrinting() + " ");
		}
		if (node.isPresentSysMLNameAndTypePart()) {
			print(node.getSysMLNameAndTypePart().getName() + " ");
		}
	}

	@Override
	public void visit(ASTSatisfyRequirementUsage node) {
		println("");
		if (node.isPresentSubset()) {
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting());
			}
			if (node.isPresentTypePart()) {
				print(node.getTypePart().toString() + " ");
			}
			if (node.isPresentSymbol()) {
				print("as ");
			}
			print(node.getSubset().toString());
		} else {
			print("requirement ");
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName() + " ");
			}
			print(node.getTypePart().toString() + " ");
		}
		if (node.isPresentSatisfactionConnectorMember()) {
			print("by ");
		}
	}
}
