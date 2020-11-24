package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.requirementdiagram._ast.*;
import de.monticore.lang.sysml.requirementdiagram._visitor.RequirementDiagramHandler;
import de.monticore.lang.sysml.requirementdiagram._visitor.RequirementDiagramTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerRequirementDiagram2 implements RequirementDiagramHandler {
	private IndentPrinter printer;
	private RequirementDiagramTraverser traverser;

	public PrettyPrinterHandlerRequirementDiagram2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public RequirementDiagramTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(RequirementDiagramTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTRequirementDefParameterList node) {
		printer.println("");
		printer.print("(");
		if (node.isPresentEmptyParameterMember()) {
			getTraverser().handle(node.getEmptyParameterMember());
		} else {
			for (int i = 0; i < node.getParameterMemberList().size(); i++) {
				getTraverser().handle(node.getParameterMember(i));
				if (i + 1 < node.getParameterMemberList().size()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");
	}

	@Override
	public void handle(ASTRequirementConstraintUsage node) {
		printer.println("");
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			printer.print("as ");
			getTraverser().handle(node.getSubset());
		} else {
			printer.print("constraint ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
		}
		getTraverser().handle(node.getConstraintParameterPart());
		getTraverser().handle(node.getConstraintBody());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberRequirementUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("requirement ");
		} else {
			printer.print("ref requirement ");
		}
		getTraverser().handle(node.getRequirementUsage());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberSatisfyRequirementUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		printer.print("satisfy ");
		getTraverser().handle(node.getSatisfyRequirementUsage());
	}

	@Override
	public void handle(ASTSatisfyRequirementUsage node) {
		printer.println("");
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			printer.print("as ");
			getTraverser().handle(node.getSubset());
		} else {
			printer.print("requirement ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			getTraverser().handle(node.getTypePart());
		}
		if (node.isPresentSatisfactionConnectorMember()) {
			printer.print("by ");
			getTraverser().handle(node.getSatisfactionConnectorMember());
		}
	}
}

