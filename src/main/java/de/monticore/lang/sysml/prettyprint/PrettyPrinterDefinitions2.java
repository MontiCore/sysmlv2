package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.definitions._ast.ASTDefinitionBodyEmpty;
import de.monticore.lang.sysml.advanced.definitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.advanced.definitions._visitor.DefinitionsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterDefinitions2 implements DefinitionsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterDefinitions2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTDefinitionBodyStd node) {
		printer.println("");
		printer.print("{");
		printer.indent();
	}

	@Override
	public void endVisit(ASTDefinitionBodyStd node) {
		printer.println("");
		printer.unindent();
		printer.print("}");
	}

	@Override
	public void visit(ASTDefinitionBodyEmpty node) {
		printer.println("");
		printer.print(";");
	}
}
