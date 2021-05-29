/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ad._ast.*;
import de.monticore.lang.sysml.ad._visitor.ADHandler;
import de.monticore.lang.sysml.ad._visitor.ADTraverser;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTSubset;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerAD2 implements ADHandler {
	private IndentPrinter printer;
	private ADTraverser traverser;

	public PrettyPrinterHandlerAD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public ADTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(ADTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTInitialNodeMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		printer.print("first ");
		node.getMemberFeature().accept(getTraverser());
		printer.print(";");
	}

	@Override
	public void handle(ASTParameterListStd node) {
		printer.print("(");
		if (!node.isEmptyParameterMembers()) {
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
	public void handle(ASTBehaviorUsageMemberPerformActionUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		printer.print("perform ");
		node.getPerformActionUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberActionUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
		printer.print(" ");
		node.getActionUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTActionParameterListStd node) {
		printer.print("(");
		if (!node.isEmptyActionParameterMemberAndFlowMembers()) {
			for (int i = 0; i < node.getActionParameterMemberAndFlowMemberList().size(); i++) {
				node.getActionParameterMemberAndFlowMember(i).accept(getTraverser());
				if (i + 1 < node.getActionParameterMemberAndFlowMemberList().size()) {
					printer.print(", ");
				}
			}
		}
		printer.print(") ");
	}

	@Override
	public void handle(ASTActionParameterFlow node) {
		printer.print("flow ");
		node.getEmptyItemFeatureMember().accept(getTraverser());
		printer.print("from ");
		node.getItemFlowEndMember().accept(getTraverser());
	}

	@Override
	public void handle(ASTPerformActionUsageDeclaration node) {
		if (!node.isEmptySubsets()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			if (node.isPresentSysMLName() || node.isPresentTypePart()) {
				printer.print("as ");
			}
			for (ASTSubset s :
				node.getSubsetList()) {
				s.accept(getTraverser());
			}
		} else {
			printer.print("action ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
		}
		if (node.isPresentActionParameterList()) {
			node.getActionParameterList().accept(getTraverser());
		}
		node.getSubsettingPart().accept(getTraverser());
		if (node.isPresentValuePart()) {
			node.getValuePart().accept(getTraverser());
		}

	}

	@Override
	public void handle(ASTParameterMemberStd node) {
		if (node.isPresentDirection()) {
			node.getDirection().accept(getTraverser());
		}
		printer.print(node.getMemberName().getNameForPrettyPrinting() + " ");
		node.getParameter().accept(getTraverser());
	}

	@Override
	public void handle(ASTAcceptActionNodeDeclaration node) {
		printer.println("");
		node.getEmptyParameterMember().accept(getTraverser());
		printer.print(" accept ");
		if (node.isPresentSysMLNameAndTypePart()) {
			node.getSysMLNameAndTypePart().accept(getTraverser());
		}
		printer.print("(");
		node.getItemFeatureMember().accept(getTraverser());
		printer.print(") ");
	}

	@Override
	public void handle(ASTSendActionNodeDeclaration node) {
		printer.println("");
		node.getEmptyParameterMember().accept(getTraverser());
		node.getEmptyItemFeatureMember().accept(getTraverser());
		printer.print("send ");
		if (node.isPresentSysMLNameAndTypePart()) {
			node.getSysMLNameAndTypePart().accept(getTraverser());
			printer.print("of ");
		}
		node.getExpressionMember(0).accept(getTraverser());
		printer.print(" to ");
		node.getExpressionMember(1).accept(getTraverser());
	}

	@Override
	public void handle(ASTActionParameterMember node) {
		if (node.isPresentMemberName()) {
			if (node.isPresentDirection()) {
				node.getDirection().accept(getTraverser());
			}
			printer.print(node.getMemberName().getNameForPrettyPrinting() + " ");
		} else {
			node.getDirection().accept(getTraverser());
		}
		node.getActionParameter().accept(getTraverser());
	}
}
