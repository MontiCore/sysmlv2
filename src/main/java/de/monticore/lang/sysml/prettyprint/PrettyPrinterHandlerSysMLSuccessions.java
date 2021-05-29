/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlitemflows._ast.*;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsHandler;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsTraverser;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTEffectBehaviourUsage;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTGuardedSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTGuardedTargetSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTSuccessionDeclaration;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTTargetTransitionStep;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTTransitionStep;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._visitor.SysMLSuccessionsHandler;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._visitor.SysMLSuccessionsTraverser;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerSysMLSuccessions implements SysMLSuccessionsHandler {
	private IndentPrinter printer;
	private SysMLSuccessionsTraverser traverser;

	public PrettyPrinterHandlerSysMLSuccessions(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLSuccessionsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLSuccessionsTraverser realThis) {
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

}
