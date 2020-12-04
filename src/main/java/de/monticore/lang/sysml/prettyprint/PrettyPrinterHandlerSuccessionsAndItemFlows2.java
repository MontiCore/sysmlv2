package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.successionsanditemflows._ast.*;
import de.monticore.lang.sysml.advanced.successionsanditemflows._visitor.SuccessionsAndItemFlowsHandler;
import de.monticore.lang.sysml.advanced.successionsanditemflows._visitor.SuccessionsAndItemFlowsTraverser;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerSuccessionsAndItemFlows2 implements SuccessionsAndItemFlowsHandler {
	private IndentPrinter printer;
	private SuccessionsAndItemFlowsTraverser traverser;

	public PrettyPrinterHandlerSuccessionsAndItemFlows2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SuccessionsAndItemFlowsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SuccessionsAndItemFlowsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTSuccessionDeclaration node) {
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			printer.print("first ");
		}
		node.getConnectorEndMember(0).accept(getTraverser());
		printer.print("then ");
		node.getConnectorEndMember(1).accept(getTraverser());
	}

	@Override
	public void handle(ASTGuardedSuccession node) {
		printer.println("");
		printer.print("succession ");
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			printer.print("first ");
		}
		node.getTransitionSourceMember().accept(getTraverser());
		node.getGuardExpressionMember().accept(getTraverser());
		printer.print("then ");
		node.getTransitionSuccessionMember().accept(getTraverser());
	}

	@Override
	public void handle(ASTGuardedTargetSuccession node) {
		printer.println("");
		node.getGuardExpressionMember().accept(getTraverser());
		printer.print("then ");
		node.getTransitionSuccessionMember().accept(getTraverser());
	}

	@Override
	public void handle(ASTTransitionStep node) {
		printer.println("");
		printer.print("transition ");
		if (node.isPresentSysMLNameAndTypePart()) {
			node.getSysMLNameAndTypePart().accept(getTraverser());
		}
		printer.print("first ");
		node.getTransitionSourceMember().accept(getTraverser());
		if (node.isPresentTriggerStepMember()) {
			node.getTriggerStepMember().accept(getTraverser());
		}
		if (node.isPresentGuardExpressionMember()) {
			node.getGuardExpressionMember().accept(getTraverser());
		}
		if (node.isPresentEffectBehaviorMember()) {
			node.getEffectBehaviorMember().accept(getTraverser());
		}
		printer.print("then ");
		node.getTransitionSuccessionMember().accept(getTraverser());
	}

	@Override
	public void handle(ASTTargetTransitionStep node) {
		printer.println("");
		if (node.isPresentTriggerStepMember()) {
			node.getTriggerStepMember().accept(getTraverser());
		}
		if (node.isPresentGuardExpressionMember()) {
			node.getGuardExpressionMember().accept(getTraverser());
		}
		if (node.isPresentEffectBehaviorMember()) {
			node.getEffectBehaviorMember().accept(getTraverser());
		}
		printer.print(" then ");
		node.getTransitionSuccessionMember().accept(getTraverser());
	}

	@Override
	public void handle(ASTEffectBehaviourUsage node) {
		if (node.isPresentPerformedActionUsage()) {
			node.getPerformedActionUsage().accept(getTraverser());
		} else {
			node.getEmptyActionUsage().accept(getTraverser());
		}
		if (!node.isEmptyActivityBodyItems()) {
			printer.println("{");
			printer.indent();
			for (ASTActivityBodyItem a :
				node.getActivityBodyItemList()) {
				a.accept(getTraverser());
			}
			printer.unindent();
			printer.println("");
			printer.println("}");
		}
	}

	@Override
	public void handle(ASTItemFlowDeclaration node) {
		boolean b = false;
		if (node.isPresentSysMLName()) {
			printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
		if (node.isPresentTypePart()) {
			node.getTypePart().accept(getTraverser());
		}
		if (node.isPresentItemFeatureMember()) {
			printer.print("of ");
			node.getItemFeatureMember().accept(getTraverser());
		} else {
			node.getEmptyItemFeatureMember().accept(getTraverser());
			b = true;
		}
		if (node.isPresentEmptyItemFeatureMember() && (!b)) {
			node.getEmptyItemFeatureMember().accept(getTraverser());
		} else {
			printer.print("from ");
		}
		node.getItemFlowEndMember(0).accept(getTraverser());
		printer.print("to ");
		node.getItemFlowEndMember(1).accept(getTraverser());
	}
}
