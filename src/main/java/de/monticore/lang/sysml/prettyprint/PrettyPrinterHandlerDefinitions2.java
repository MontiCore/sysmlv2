package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.advanced.definitions._ast.ASTConjugatedPortMember;
import de.monticore.lang.sysml.advanced.definitions._ast.ASTPortMember;
import de.monticore.lang.sysml.advanced.definitions._visitor.DefinitionsHandler;
import de.monticore.lang.sysml.advanced.definitions._visitor.DefinitionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerDefinitions2 implements DefinitionsHandler {
	private IndentPrinter printer;
	private DefinitionsTraverser traverser;

	public PrettyPrinterHandlerDefinitions2(IndentPrinter printer, SysMLTraverser traverser) {
		this.printer = printer;
		this.traverser = traverser;
	}

	@Override
	public DefinitionsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(DefinitionsTraverser traverser) {
		this.traverser = traverser;
	}

	@Override
	public void handle(ASTPortMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("port ");
		node.getPortUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTConjugatedPortMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("port ");
		node.getConjugatedPortUsage().accept(getTraverser());
	}
}
