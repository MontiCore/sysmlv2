package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.successionsanditemflows._ast.*;
import de.monticore.lang.sysml.advanced.successionsanditemflows._visitor.SuccessionsAndItemFlowsVisitor2;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSuccessionsAndItemFlows2 implements SuccessionsAndItemFlowsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSuccessionsAndItemFlows2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberSuccession node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("succession ");
	}

	@Override
	public void endVisit(ASTSuccession node) {
		printer.print(";");
	}

	@Override
	public void visit(ASTTargetSuccession node) {
		printer.println("");
		printer.print("then ");
	}

	@Override
	public void visit(ASTEmptySuccession node) {
		printer.println("");
		printer.print("then ");
	}

	@Override
	public void visit(ASTDefaultTargetSuccession node) {
		printer.println("");
		printer.print("else");
	}

	@Override
	public void visit(ASTTriggerStepMember node) {
		printer.print("accept ");
	}

	@Override
	public void visit(ASTGuardExpressionMember node) {
		printer.println("");
		printer.print("if ");
	}

	@Override
	public void endVisit(ASTGuardExpressionMember node){
		printer.print(" ");
	}

	@Override
	public void visit(ASTEffectBehaviorMember node) {
		printer.println("");
		printer.print("do ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberItemFlow node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("stream ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberSuccessionItemFlow node) {
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("flow ");
	}

	@Override
	public void visit(ASTItemFeatureMember node){
		if(node.isPresentMemberName()){
			printer.print(node.getMemberName().getNameForPrettyPrinting()+" ");
		}
	}
}
