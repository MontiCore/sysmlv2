/* (c) https://github.com/MontiCore/monticore */
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
		printer.print("(");
		if (node.isPresentEmptyParameterMember()) {
			node.getEmptyParameterMember().accept(getTraverser());
		} else {
			for (int i = 0; i < node.getParameterMemberList().size(); i++) {
				node.getParameterMember(i).accept(getTraverser());
				if (i + 1 < node.getParameterMemberList().size()) {
					printer.print(", ");
				}
			}
		}
		printer.print(") ");
	}

	@Override
	public void handle(ASTRequirementConstraintUsage node) {
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			printer.print("as ");
		}
		if (node.isPresentSubset()){
			node.getSubset().accept(getTraverser());
		} else {
			printer.print("constraint ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
		}
		node.getConstraintParameterPart().accept(getTraverser());
		node.getConstraintBody().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberRequirementUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("requirement ");
		} else {
			printer.print("ref requirement ");
		}
		node.getRequirementUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberSatisfyRequirementUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		printer.print("satisfy ");
		node.getSatisfyRequirementUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTSatisfyRequirementUsage node) {
		printer.println("");
		if (node.isPresentFirstName()) {
			printer.print(node.getFirstName().getNameForPrettyPrinting() + " ");
		}
		if (node.isPresentFirstType()) {
			node.getFirstType().accept(getTraverser());
		}
		printer.print("as ");
		if (node.isPresentSubset()) {
			node.getSubset().accept(getTraverser());
		} else {
			printer.print("requirement ");
		}
			if (node.isPresentSecondName()) {
				printer.print(node.getSecondName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentSecondType()) {
				node.getSecondType().accept(getTraverser());
			}
		if (node.isPresentSatisfactionConnectorMember()) {
			printer.print("by ");
			node.getSatisfactionConnectorMember().accept(getTraverser());
		}
		node.getConstraintParameterPart().accept(getTraverser());
		node.getInvariantPart().accept(getTraverser());
		node.getRequirementBody().accept(getTraverser());
	}
}

