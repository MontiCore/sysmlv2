package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ibd._ast.ASTAssociationEndMemberPartProperty;
import de.monticore.lang.sysml.ibd._visitor.IBDHandler;
import de.monticore.lang.sysml.ibd._visitor.IBDTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerIBD2 implements IBDHandler {
	private IndentPrinter printer;
	private IBDTraverser traverser;

	@Override
	public IBDTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(IBDTraverser realThis) {
		this.traverser = realThis;
	}

	public PrettyPrinterHandlerIBD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public void handle(ASTAssociationEndMemberPartProperty node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end");
		node.getPartProperty().accept(getTraverser());
	}
}
