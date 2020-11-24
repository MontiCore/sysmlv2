package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ibd._ast.ASTAssociationEndMemberPartProperty;
import de.monticore.lang.sysml.ibd._visitor.IBDHandler;
import de.monticore.lang.sysml.ibd._visitor.IBDTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerIBD2 implements IBDHandler {
	private IndentPrinter printer;
	private IBDTraverser traverser;

	public PrettyPrinterHandlerIBD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public void handle(ASTAssociationEndMemberPartProperty node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end");
		getTraverser().handle(node.getPartProperty());
	}
}
