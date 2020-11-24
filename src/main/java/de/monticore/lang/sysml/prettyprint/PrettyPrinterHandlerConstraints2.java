package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.constraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.constraints._ast.ASTBehaviorUsageMemberAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.constraints._ast.ASTBehaviorUsageMemberConstraintUsage;
import de.monticore.lang.sysml.advanced.constraints._visitor.ConstraintsHandler;
import de.monticore.lang.sysml.advanced.constraints._visitor.ConstraintsTraverser;
import de.monticore.lang.sysml.basics.usages._ast.ASTSubset;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerConstraints2 implements ConstraintsHandler {
	private IndentPrinter printer;
	private ConstraintsTraverser traverser;

	public PrettyPrinterHandlerConstraints2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public ConstraintsTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(ConstraintsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTBehaviorUsageMemberConstraintUsage node) {
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (!node.isPresentIsComposite()) {
			printer.print("ref constraint ");
		} else {
			printer.print("constraint");
		}
		getTraverser().handle(node.getConstraintUsage());
	}

	@Override
	public void handle(ASTBehaviorUsageMemberAssertConstraintUsage node) {
		getTraverser().handle(node.getDefinitionMemberPrefix());
		printer.print("assert ");
		getTraverser().handle(node.getAssertConstraintUsage());
	}

	@Override
	public void handle(ASTAssertConstraintUsage node) {
		if (node.isEmptySubsets()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			if (node.isPresentSysMLName() || node.isPresentTypePart()) {
				printer.print("as");
			}
			for (ASTSubset s :
				node.getSubsetList()) {
				getTraverser().handle(s);
			}
		} else {
			printer.print("constraint ");
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
		}
	}
}
