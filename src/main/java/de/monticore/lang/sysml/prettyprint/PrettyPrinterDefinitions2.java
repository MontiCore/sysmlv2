package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.definitions._ast.ASTDefinitionBodyEmpty;
import de.monticore.lang.sysml.advanced.definitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.advanced.definitions._visitor.DefinitionsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterDefinitions2 extends IndentPrinter implements DefinitionsVisitor2 {
	@Override
	public void visit(ASTDefinitionBodyStd node) {
		println("");
		print("{");
		indent();
	}

	@Override
	public void endVisit(ASTDefinitionBodyStd node) {
		println("");
		unindent();
		print("}");
	}

	@Override
	public void visit(ASTDefinitionBodyEmpty node) {
		println("");
		print(";");
	}
}
