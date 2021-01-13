package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmldefinitions._visitor.SysMLDefinitionsHandler;
import de.monticore.lang.sysml.common.sysmldefinitions._visitor.SysMLDefinitionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerDefinitions2 implements SysMLDefinitionsHandler {
	private IndentPrinter printer;
	private SysMLDefinitionsTraverser traverser;

	public PrettyPrinterHandlerDefinitions2(IndentPrinter printer, SysMLTraverser traverser) {
		this.printer = printer;
		this.traverser = traverser;
	}

	@Override
	public SysMLDefinitionsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLDefinitionsTraverser traverser) {
		this.traverser = traverser;
	}

}
