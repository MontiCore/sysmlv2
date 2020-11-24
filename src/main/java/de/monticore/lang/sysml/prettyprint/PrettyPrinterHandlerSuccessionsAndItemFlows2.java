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
		printer.println("");
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			printer.print("first ");
		}
		getTraverser().handle(node.getConnectorEndMember(0));
		printer.print("then ");
		getTraverser().handle(node.getConnectorEndMember(1));
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
				getTraverser().handle(node.getTypePart());
			}
			printer.print("first ");
		}
		getTraverser().handle(node.getTransitionSourceMember());
		getTraverser().handle(node.getGuardExpressionMember());
		printer.print("then ");
		getTraverser().handle(node.getTransitionSuccessionMember());
	}

	@Override
	public void handle(ASTGuardedTargetSuccession node) {
		printer.println("");
		getTraverser().handle(node.getGuardExpressionMember());
		printer.print("then ");
		getTraverser().handle(node.getTransitionSuccessionMember());
	}

	@Override
	public void handle(ASTTransitionStep node) {
		printer.println("");
		printer.print("transition ");
		if (node.isPresentSysMLNameAndTypePart()) {
			getTraverser().handle(node.getSysMLNameAndTypePart());
		}
		printer.print("first ");
		getTraverser().handle(node.getTransitionSourceMember());
		if (node.isPresentTriggerStepMember()) {
			getTraverser().handle(node.getTriggerStepMember());
		}
		if (node.isPresentGuardExpressionMember()) {
			getTraverser().handle(node.getGuardExpressionMember());
		}
		if (node.isPresentEffectBehaviorMember()) {
			getTraverser().handle(node.getEffectBehaviorMember());
		}
		printer.print("then ");
		getTraverser().handle(node.getTransitionSuccessionMember());
	}

	@Override
	public void handle(ASTTargetTransitionStep node) {
		printer.println("");
		if (node.isPresentTriggerStepMember()) {
			getTraverser().handle(node.getTriggerStepMember());
		}
		if (node.isPresentGuardExpressionMember()) {
			getTraverser().handle(node.getGuardExpressionMember());
		}
		if (node.isPresentEffectBehaviorMember()) {
			getTraverser().handle(node.getEffectBehaviorMember());
		}
		printer.print("then ");
		getTraverser().handle(node.getTransitionSuccessionMember());
	}

	@Override
	public void handle(ASTEffectBehaviourUsage node) {
		if (node.isPresentPerformedActionUsage()){
			getTraverser().handle(node.getPerformedActionUsage());
			if (!node.isEmptyActivityBodyItems()){
				printer.println("{");
				printer.indent();
				for (ASTActivityBodyItem a:
						 node.getActivityBodyItemList()) {
					getTraverser().handle(a);
				}
				printer.unindent();
				printer.println("");
				printer.println("}");
			}
		}
	}

	@Override
	public void handle(ASTItemFlowDeclaration node) {
		printer.println("");
		if (node.isPresentSysMLName()){
			printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
		}
		if (node.isPresentTypePart()){
			getTraverser().handle(node.getTypePart());
		}
		if (node.isPresentItemFeatureMember()){
			printer.print("of ");
			getTraverser().handle(node.getItemFeatureMember());
		} else {
			getTraverser().handle(node.getEmptyItemFeatureMember());
		}
		if (node.isPresentEmptyItemFeatureMember()){
			getTraverser().handle(node.getEmptyItemFeatureMember());
		} else {
			printer.print("from ");
		}
		getTraverser().handle(node.getItemFlowEndMember(0));
		printer.print("to ");
		getTraverser().handle(node.getItemFlowEndMember(1));
	}
}
