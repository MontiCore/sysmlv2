package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.names._ast.ASTColonQualifiedName;
import de.monticore.lang.sysml.basics.sysmldefault.names._ast.ASTDotQualifiedName;
import de.monticore.lang.sysml.basics.sysmldefault.names._ast.ASTSimpleName;
import de.monticore.lang.sysml.basics.sysmldefault.names._visitor.NamesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrintNames2 implements NamesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrintNames2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTSimpleName node) {
		printer.print(node.getSysMLName().getNameForPrettyPrinting() +" ");
	}

	@Override
	public void visit(ASTColonQualifiedName node) {
		for (int i = 0; i < node.getSysMLNameList().size(); i++) {
			printer.print(node.getSysMLName(i).getNameForPrettyPrinting()+" ");
			if (i == node.getSysMLNameList().size() - 1) {
				printer.print("");
			} else {
				printer.print("::");
			}
		}
	}

	@Override
	public void visit(ASTDotQualifiedName node) {
		for (int i = 0; i < node.getSysMLNameList().size(); i++) {
			printer.print(node.getSysMLName(i).getNameForPrettyPrinting()+" ");
			if (i == node.getSysMLNameList().size() - 1) {
				printer.print("");
			} else {
				printer.print(".");
			}
		}
	}
}
