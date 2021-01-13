package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTBehaviorUsageMemberAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTBehaviorUsageMemberConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._visitor.SysMLConstraintsHandler;
import de.monticore.lang.sysml.advanced.sysmlconstraints._visitor.SysMLConstraintsTraverser;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTSubset;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerConstraints2 implements SysMLConstraintsHandler {
	private IndentPrinter printer;
	private SysMLConstraintsTraverser traverser;

	public PrettyPrinterHandlerConstraints2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLConstraintsTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(SysMLConstraintsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTBehaviorUsageMemberConstraintUsage node) {
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (!node.isPresentIsComposite()) {
			printer.print("ref constraint ");
		} else {
			printer.print("constraint ");
		}
		node.getConstraintUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberAssertConstraintUsage node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		printer.print("assert ");
		node.getAssertConstraintUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTAssertConstraintUsage node) {
		if (!node.isEmptySubsets()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			if (node.isPresentSysMLName() || node.isPresentTypePart()) {
				printer.print("as");
			}
			for (ASTSubset s :
				node.getSubsetList()) {
				s.accept(getTraverser());
			}
		} else {
			printer.print("constraint ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
		}
		node.getConstraintParameterPart().accept(getTraverser());
		node.getInvariantPart().accept(getTraverser());
		node.getConstraintBody().accept(getTraverser());
	}
}
