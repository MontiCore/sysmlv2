package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ad._ast.*;
import de.monticore.lang.sysml.ad._visitor.ADHandler;
import de.monticore.lang.sysml.ad._visitor.ADTraverser;
import de.monticore.lang.sysml.basics.usages._ast.ASTSubset;
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
		getTraverser().handle(node.getDefinitionMemberPrefix());
		printer.print("first ");
		getTraverser().handle(node.getMemberFeature());
		printer.print(";");
	}

	@Override
	public void handle(ASTParameterListStd node) {
		printer.println("");
		printer.print("(");
		if (!node.isEmptyParameterMembers()) {
			for (int i = 0; i < node.getParameterMemberList().size(); i++) {
				getTraverser().handle(node.getParameterMember(i));
				if (i + 1 < node.getParameterMemberList().size()) {
					printer.print("; ");
				}
			}
		}
		printer.print(")");
	}

	@Override
	public void handle(ASTBehaviorUsageMemberPerformActionUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		printer.print("perform ");
		getTraverser().handle(node.getPerformActionUsage());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberActionUsage node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			printer.print("action ");
		} else {
			printer.print("ref action ");
		}
		printer.print(" ");
		getTraverser().handle(node.getActionUsage());
	}

	@Override
	public void handle(ASTActionParameterListStd node) {
		printer.println("");
		printer.print("(");
		if (!node.isEmptyActionParameterMemberAndFlowMembers()) {
			for (int i = 0; i < node.getActionParameterMemberAndFlowMemberList().size(); i++) {
				getTraverser().handle(node.getActionParameterMemberAndFlowMember(i));
				if (i + 1 < node.getActionParameterMemberAndFlowMemberList().size()) {
					printer.print("; ");
				}
			}
		}
		printer.print(")");
	}

	@Override
	public void handle(ASTActionParameterFlow node) {
		printer.println("");
		printer.print("flow ");
		getTraverser().handle(node.getEmptyItemFeatureMember());
		printer.print("from ");
		getTraverser().handle(node.getItemFlowEndMember());
	}

	@Override
	public void handle(ASTPerformActionUsageDeclaration node) {
		printer.println("");
		if (node.isPresentSysMLName()) {
			printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			printer.print("as ");
			for (ASTSubset s :
				node.getSubsetList()) {
				getTraverser().handle(s);
			}
		} else {
			printer.print("action ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
		}
		if (node.isPresentActionParameterList()) {
			getTraverser().handle(node.getActionParameterList());
		}
		getTraverser().handle(node.getSubsettingPart());
		if (node.isPresentValuePart()) {
			getTraverser().handle(node.getValuePart());
		}
	}

	@Override
	public void handle(ASTAcceptActionNodeDeclaration node) {
		printer.println("");
		getTraverser().handle(node.getEmptyParameterMember());
		printer.print(" accept ");
		if (node.isPresentSysMLNameAndTypePart()) {
			getTraverser().handle(node.getSysMLNameAndTypePart());
			printer.print(" as ");
		}
	}

	@Override
	public void handle(ASTSendActionNodeDeclaration node) {
		printer.println("");
		getTraverser().handle(node.getEmptyParameterMember());
		getTraverser().handle(node.getEmptyItemFeatureMember());
		printer.print("send ");
		if (node.isPresentSysMLNameAndTypePart()) {
			getTraverser().handle(node.getSysMLNameAndTypePart());
			printer.print("of ");
		}
		getTraverser().handle(node.getExpressionMember(0));
		printer.print("to ");
		getTraverser().handle(node.getExpressionMember(1));
	}

	@Override
	public void handle(ASTActionParameterMember node) {
		printer.println("");
		if (node.isPresentMemberName()) {
			if (node.isPresentDirection()) {
				getTraverser().handle(node.getDirection());
			}
			printer.print(node.getMemberName().getNameForPrettyPrinting() + " ");
		} else {
			getTraverser().handle(node.getDirection());
		}
		getTraverser().handle(node.getActionParameter());
	}
}
