package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._visitor.SysMLSharedVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSharedBasis2 implements SysMLSharedVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSharedBasis2 (IndentPrinter print){
		this.printer= print;
	}

}
