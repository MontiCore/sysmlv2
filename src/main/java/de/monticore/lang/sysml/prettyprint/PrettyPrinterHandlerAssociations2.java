/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTConjugatedEndPortMember;
import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTEndPortMember;
import de.monticore.lang.sysml.common.sysmlassociations._visitor.SysMLAssociationsHandler;
import de.monticore.lang.sysml.common.sysmlassociations._visitor.SysMLAssociationsTraverser;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerAssociations2 implements SysMLAssociationsHandler {
	private IndentPrinter printer;
	private SysMLAssociationsTraverser traverser;

	public PrettyPrinterHandlerAssociations2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLAssociationsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLAssociationsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTEndPortMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()){
			printer.print("abstract ");
		}
		printer.print("end port ");
		node.getPortUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTConjugatedEndPortMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()){
			printer.print("abstract ");
		}
		printer.print("end port ");
		node.getConjugatedPortUsage().accept(getTraverser());
	}
}
