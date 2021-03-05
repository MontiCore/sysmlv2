package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlshared._visitor.SysMLSharedVisitor2;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTSysMLNameAndTypePart;
import de.monticore.lang.sysml.common.sysmlusages._visitor.SysMLUsagesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSysMLUsage implements SysMLUsagesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSysMLUsage (IndentPrinter print){
		this.printer= print;
	}

	@Override
	public void visit(ASTSysMLNameAndTypePart node) {
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}
}
