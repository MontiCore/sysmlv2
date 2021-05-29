/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.ad._visitor.ADTraverser;
import de.monticore.lang.sysml.bdd._ast.ASTAssociationEndMemberReferenceProperty;
import de.monticore.lang.sysml.bdd._ast.ASTBlockDeclaration;
import de.monticore.lang.sysml.bdd._ast.ASTReferencePropertyNonPortStructureUsageMember;
import de.monticore.lang.sysml.bdd._visitor.BDDHandler;
import de.monticore.lang.sysml.bdd._visitor.BDDTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerBDD2 implements BDDHandler {
	private IndentPrinter printer;
	private BDDTraverser traverser;

	public PrettyPrinterHandlerBDD2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public BDDTraverser getTraverser() {
		return traverser;
	}

	@Override
	public void setTraverser(BDDTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTAssociationEndMemberReferenceProperty node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isRef()) {
			printer.print("ref ");
		}
		node.getReferenceProperty().accept(getTraverser());
	}
}
