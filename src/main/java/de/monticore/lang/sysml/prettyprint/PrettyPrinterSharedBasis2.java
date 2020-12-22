package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTSysMLNameAndTypePart;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._visitor.SharedBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterSharedBasis2 implements SharedBasisVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterSharedBasis2 (IndentPrinter print){
		this.printer= print;
	}

	@Override
	public void visit(ASTSysMLNameAndTypePart node) {
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}
}
