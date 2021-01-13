package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlitemflows._ast.*;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsVisitor2;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSysMLItemFlows2 implements SysMLItemFlowsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSysMLItemFlows2(IndentPrinter print) {
		this.printer = print;
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
