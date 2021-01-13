package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlitemflows._ast.*;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsVisitor2;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTDefaultTargetSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTEffectBehaviorMember;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTEmptySuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTGuardExpressionMember;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTNonPortStructureUsageMemberSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTTargetSuccession;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._ast.ASTTriggerStepMember;
import de.monticore.lang.sysml.advanced.sysmlsuccessions._visitor.SysMLSuccessionsVisitor2;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSysMLSuccessions implements SysMLSuccessionsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSysMLSuccessions(IndentPrinter print) {
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

}
