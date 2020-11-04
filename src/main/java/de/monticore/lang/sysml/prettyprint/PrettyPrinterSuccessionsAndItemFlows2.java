package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.successionsanditemflows._ast.*;
import de.monticore.lang.sysml.advanced.successionsanditemflows._visitor.SuccessionsAndItemFlowsVisitor2;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSuccessionsAndItemFlows2 extends IndentPrinter implements SuccessionsAndItemFlowsVisitor2 {
	@Override
	public void visit(ASTNonPortStructureUsageMemberSuccession node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("succession ");
	}

	@Override
	public void visit(ASTSuccession node) {
		println("");
	}

	@Override
	public void endVisit(ASTSuccession node) {
		print(";");
	}

	@Override
	public void visit(ASTSuccessionDeclaration node) {
		println("");
		if (node.isPresentSymbol()) {
			if (node.isPresentSysMLName()) {
				print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				print(node.getTypePart().toString() + " ");
			}
			print("first ");
		}
		print(node.getConnectorEndMember(0).toString() + "then ");
	}

	@Override
	public void visit(ASTTargetSuccession node) {
		println("");
		print("then ");
	}

	@Override
	public void visit(ASTEmptySuccession node) {
		println("");
		print("then ");
	}

	@Override
	public void visit(ASTGuardedSuccession node) {
		println("");
		print("succession ");
		if (node.isPresentSymbol()) {
			print(node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
	}

	@Override
	public void endVisit(ASTGuardedSuccession node) {
		if (node.isPresentSymbol()) {
			print("first ");
		}
	}

	@Override
	public void visit(ASTGuardedTargetSuccession node) {
		println("");
		print(node.getGuardExpressionMember().toString() + " ");
		print("then ");
	}

	@Override
	public void visit(ASTDefaultTargetSuccession node) {
		println("");
		print("else");
	}

	@Override
	public void visit(ASTTransitionStep node) {
		println("");
		print("transition ");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(node.getSysMLNameAndTypePart().toString() + " first ");
		}
	}

	@Override
	public void visit(ASTTargetTransitionStep node) {
		println("");
	}

	@Override
	public void endVisit(ASTTargetTransitionStep node) {
		print("then ");
		print(node.getTransitionSuccessionMember().toString());
	}

	@Override
	public void visit(ASTEffectBehaviourUsage node) {
		println("");

	}

	@Override
	public void endVisit(ASTEffectBehaviourUsage node) {
		if (!node.isEmptyActivityBodyItems()) {
			println("{");
			indent();
			for (ASTActivityBodyItem a : node.getActivityBodyItemList()) {
				println(a.toString());
			}
			unindent();
			println("");
			print("}");
		}
	}

	@Override
	public void visit(ASTTriggerStepMember node) {
		println("");
		print("accept ");
	}

	@Override
	public void visit(ASTGuardExpressionMember node) {
		println("");
		print("if ");
	}

	@Override
	public void visit(ASTEffectBehaviorMember node) {
		println("");
		print("do ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberItemFlow node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("stream ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberSuccessionItemFlow node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("flow ");
	}

	@Override
	public void visit(ASTItemFlowDeclaration node) {
		println("");
		if (node.isPresentSysMLName()) {
			print(node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
		if (node.isPresentTypePart()) {
			print(node.getTypePart().toString());
		}
		if (node.isPresentItemFeatureMember()) {
			print("of " + node.getItemFeatureMember());
		} else {
			print(node.getEmptyItemFeatureMember().toString());
		}
		if (node.isPresentSymbol()) {
			print("from ");
		}
	}

	@Override
	public void endVisit(ASTItemFlowDeclaration node) {
		print("to ");
		print(node.getItemFlowEndMember(1).toString()+" ");
	}
}
