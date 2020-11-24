package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.associations._ast.ASTConjugatedEndPortMember;
import de.monticore.lang.sysml.basics.associations._ast.ASTEndPortMember;
import de.monticore.lang.sysml.basics.associations._visitor.AssociationsHandler;
import de.monticore.lang.sysml.basics.associations._visitor.AssociationsTraverser;
import de.monticore.lang.sysml.basics.sysmldefault.expressions._visitor.ExpressionsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerAssociations2 implements AssociationsHandler {
	private IndentPrinter printer;
	private AssociationsTraverser traverser;

	public PrettyPrinterHandlerAssociations2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public AssociationsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(AssociationsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTEndPortMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()){
			printer.print("abstract ");
		}
		printer.print("end port ");
		getTraverser().handle(node.getPortUsage());
	}

	@Override
	public void handle(ASTConjugatedEndPortMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()){
			printer.print("abstract ");
		}
		printer.print("end port ");
		getTraverser().handle(node.getConjugatedPortUsage());
	}
}
