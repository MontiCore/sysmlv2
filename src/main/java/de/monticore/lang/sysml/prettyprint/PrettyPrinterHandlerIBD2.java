package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.lang.sysml.sysmlibd._ast.ASTAssociationEndMemberPartProperty;
import de.monticore.lang.sysml.sysmlibd._visitor.SysMLIBDHandler;
import de.monticore.lang.sysml.sysmlibd._visitor.SysMLIBDTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerIBD2 implements SysMLIBDHandler {
	private IndentPrinter printer;
	private SysMLIBDTraverser traverser;

	@Override
	public SysMLIBDTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLIBDTraverser realThis) {
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
