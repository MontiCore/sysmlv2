package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlitemflows._ast.*;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsHandler;
import de.monticore.lang.sysml.advanced.sysmlitemflows._visitor.SysMLItemFlowsTraverser;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTActivityBodyItem;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerSysMLItemFlows2 implements SysMLItemFlowsHandler {
	private IndentPrinter printer;
	private SysMLItemFlowsTraverser traverser;

	public PrettyPrinterHandlerSysMLItemFlows2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLItemFlowsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLItemFlowsTraverser realThis) {
		this.traverser = realThis;
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
