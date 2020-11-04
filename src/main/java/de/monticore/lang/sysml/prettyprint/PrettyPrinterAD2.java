package de.monticore.lang.sysml.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysml.ad._ast.*;
import de.monticore.lang.sysml.ad._visitor.ADVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterAD2 extends IndentPrinter implements ADVisitor2 {

	@Override
	public void visit(ASTActivityUnit node) {
		println("");
	}

	@Override
	public void endVisit(ASTActivityUnit node) {

	}

	@Override
	public void visit(ASTActivity node) {
		println("");

	}

	@Override
	public void endVisit(ASTActivity node) {

	}

	@Override
	public void visit(ASTActivityDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("activity " + node.getSysMLName().getNameForPrettyPrinting());
	}

	@Override
	public void endVisit(ASTActivityDeclaration node) {
	}

	@Override
	public void visit(ASTParameterListStd node) {
		println("");
		print("(");
	}

	@Override
	public void endVisit(ASTParameterListStd node) {
		print(")");
	}

	@Override
	public void visit(ASTActivityBodyStd node) {
		if (node.isEmptyActivityBodyItems()) {
			println(";");
		} else {
			println("{");
			indent();
		}
	}

	@Override
	public void endVisit(ASTActivityBodyStd node) {
		if (!node.isEmptyActivityBodyItems()) {
			unindent();
			println("");
			println("}");
		}
	}

	@Override
	public void visit(ASTActivityBodyItemStd node) {
		println("");
	}

	@Override
	public void endVisit(ASTActivityBodyItemStd node) {

	}

	@Override
	public void visit(ASTParameterMemberStd node) {
		println("");
	}

	@Override
	public void endVisit(ASTParameterMemberStd node) {

	}

	@Override
	public void visit(ASTInitialNodeMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTInitialNodeMember node) {

		print(" first "+node.getMemberFeature().toString());
		print(";");
	}

	@Override
	public void visit(ASTActivityNodeMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTActivityNodeMember node) {

	}

	@Override
	public void visit(ASTTargetSuccessionMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTTargetSuccessionMember node) {
		print(";");
	}

	@Override
	public void visit(ASTGuardedSuccessionMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTGuardedSuccessionMember node) {
		print(";");
	}

	@Override
	public void visit(ASTAssociationEndMemberActionUsage node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isPresentIsComposite()) {
			print("action ");
		} else {
			print("ref action ");
		}
		print("end ");
	}

	@Override
	public void endVisit(ASTAssociationEndMemberActionUsage node) {

	}

	@Override
	public void visit(ASTActionUnit node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionUnit node) {

	}

	@Override
	public void visit(ASTActionDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("action");
	}

	@Override
	public void endVisit(ASTActionDeclaration node) {

	}

	@Override
	public void visit(ASTActionUsageStd node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionUsageStd node) {

	}

	@Override
	public void visit(ASTActionUsagePackagedUsageMember node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		print("action ");
	}

	@Override
	public void endVisit(ASTActionUsagePackagedUsageMember node) {

	}

	@Override
	public void visit(ASTBehaviorUsageMemberActionUsage node) {
		println("");
	}

	@Override
	public void endVisit(ASTBehaviorUsageMemberActionUsage node) {

	}

	@Override
	public void visit(ASTBehaviorUsageMemberPerformActionUsage node) {
		println("");
		print(node.getDefinitionMemberPrefix().toString() + " perform ");
	}

	@Override
	public void endVisit(ASTBehaviorUsageMemberPerformActionUsage node) {

	}

	@Override
	public void visit(ASTActionParameterListStd node) {
		println("");
		print("(");
	}

	@Override
	public void endVisit(ASTActionParameterListStd node) {
		print(")");
	}

	@Override
	public void visit(ASTActionParameterMemberAndFlowMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionParameterMemberAndFlowMember node) {

	}

	@Override
	public void visit(ASTActionParameter node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionParameter node) {

	}

	@Override
	public void visit(ASTActionParameterFlow node) {
		println("");
		//TODO
	}

	@Override
	public void endVisit(ASTActionParameterFlow node) {

	}

	@Override
	public void visit(ASTPerformActionUsage node) {
		println("");
	}

	@Override
	public void endVisit(ASTPerformActionUsage node) {

	}

	@Override
	public void visit(ASTPerformActionUsageDeclaration node) {
		println("");
		if (node.isPresentSysMLName()) {
			print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			if (node.isPresentTypePart()) {
				print(node.getTypePart().toString() + " "); //TODO
			}
			print("as ");
		} else {
			print("action ");
		}
	}

	@Override
	public void endVisit(ASTPerformActionUsageDeclaration node) {

	}

	@Override
	public void visit(ASTActivityNode node) {
		println("");
	}

	@Override
	public void endVisit(ASTActivityNode node) {

	}

	@Override
	public void visit(ASTActionNode node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionNode node) {

	}

	@Override
	public void visit(ASTActionNodeDeclaration node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionNodeDeclaration node) {

	}

	@Override
	public void visit(ASTActionUsageNodeDeclaration node) {
		println("");
		if (node.isAbstract()) {
			print("abstract ");
		}
		if (node.isIsComposite()) {
			print("action ");
		} else {
			print("ref action ");
		}
	}

	@Override
	public void endVisit(ASTActionUsageNodeDeclaration node) {

	}

	@Override
	public void visit(ASTPerformActionNodeDeclaration node) {
		println("");
		if (node.isIsComposite()) {
			print("perform ");
		}
		if (node.isPresentSysMLNameAndTypePart()) {
			print(node.getSysMLNameAndTypePart().getName() + " as ");
		}
		print(node.getValuePart().toString());
	}

	@Override
	public void endVisit(ASTPerformActionNodeDeclaration node) {

	}

	@Override
	public void visit(ASTAcceptActionNodeDeclaration node) {
		println("");
		print(node.getEmptyParameterMember().toString() + " accept ");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(node.getSysMLNameAndTypePart().toString() + " as ");
		}
	}

	@Override
	public void endVisit(ASTAcceptActionNodeDeclaration node) {

	}

	@Override
	public void visit(ASTSendActionNodeDeclaration node) {
		println("");
		print(node.getEmptyParameterMember() + " " + node.getEmptyItemFeatureMember() + "send ");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(node.getSysMLNameAndTypePart().toString() + " of ");
		}
		print(node.getExpressionMember(0).toString() + " to " + node.getExpressionMember(1).toString());
	}

	@Override
	public void endVisit(ASTSendActionNodeDeclaration node) {

	}

	@Override
	public void visit(ASTControlNode node) {
		println("");
	}

	@Override
	public void endVisit(ASTControlNode node) {

	}

	@Override
	public void visit(ASTMergeNode node) {
		println("");
		print("merge");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(" " + node.getSysMLNameAndTypePart().getName());
		}
		print(";");
	}

	@Override
	public void endVisit(ASTMergeNode node) {
	}

	@Override
	public void visit(ASTDecisionNode node) {
		println("");
		print("decide");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(" " + node.getSysMLNameAndTypePart().getName());
		}
		print(";");
	}

	@Override
	public void endVisit(ASTDecisionNode node) {

	}

	@Override
	public void visit(ASTJoinNode node) {
		println("");
		print("join");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(" " + node.getSysMLNameAndTypePart().getName());
		}
		print(";");
	}

	@Override
	public void endVisit(ASTJoinNode node) {

	}

	@Override
	public void visit(ASTForkNode node) {
		println("");
		print("fork");
		if (node.isPresentSysMLNameAndTypePart()) {
			print(" " + node.getSysMLNameAndTypePart().getName());
		}
		print(";");
	}

	@Override
	public void endVisit(ASTForkNode node) {

	}

	@Override
	public void visit(ASTActionParameterMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionParameterMember node) {
		if (node.isPresentMemberName()){
			print(node.getMemberName().getNameForPrettyPrinting());
		}
	}

	@Override
	public void visit(ASTActionParameterFlowMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTActionParameterFlowMember node) {

	}

	@Override
	public void visit(ASTEmptySuccessionMember node) {
		println("");
	}

	@Override
	public void endVisit(ASTEmptySuccessionMember node) {

	}

	@Override
	public void visit(ASTPerformedActionUsageStd node) {
		println("");
	}

	@Override
	public void endVisit(ASTPerformedActionUsageStd node) {

	}

	@Override
	public void visit(ASTADNode node) {
		println("");
	}

	@Override
	public void endVisit(ASTADNode node) {

	}
}
