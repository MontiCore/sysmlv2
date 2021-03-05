package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.lang.sysml.sysmlbdd._ast.ASTAssociationEndMemberReferenceProperty;
import de.monticore.lang.sysml.sysmlbdd._visitor.SysMLBDDHandler;
import de.monticore.lang.sysml.sysmlbdd._visitor.SysMLBDDTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerBDD2 implements SysMLBDDHandler {
	private IndentPrinter printer;
	private SysMLBDDTraverser traverser;

	public PrettyPrinterHandlerBDD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLBDDTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(SysMLBDDTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTAssociationEndMemberReferenceProperty node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isRef()) {
			printer.print("ref ");
		}
		node.getReferenceProperty().accept(getTraverser());
	}
}
