/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyEmpty;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.common.sysmldefinitions._visitor.SysMLDefinitionsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterDefinitions2 implements SysMLDefinitionsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterDefinitions2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTDefinitionBodyStd node) {
		printer.println("{");
		printer.indent();
	}

	@Override
	public void endVisit(ASTDefinitionBodyStd node) {
		printer.println("");
		printer.unindent();
		printer.println("}");
	}

	@Override
	public void visit(ASTDefinitionBodyEmpty node) {
		printer.print("; ");
	}
}
