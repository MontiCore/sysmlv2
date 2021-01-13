package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTSuperclassingList;
import de.monticore.lang.sysml.common.sysmlclassifiers._visitor.SysMLClassifiersHandler;
import de.monticore.lang.sysml.common.sysmlclassifiers._visitor.SysMLClassifiersTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerClassifiers2 implements SysMLClassifiersHandler {
	private IndentPrinter printer;
	private SysMLClassifiersTraverser traverser;

	public PrettyPrinterHandlerClassifiers2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}
	@Override
	public SysMLClassifiersTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLClassifiersTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTSuperclassingList node) {
		node.getSpecializesKeyword().accept(getTraverser());
		for (int i = 0; i < node.getQualifiedNameList().size(); i++) {
			node.getQualifiedName(i).accept(getTraverser());
			if (i + 1 < node.getQualifiedNameList().size()) {
				printer.print(", ");
			}
		}
	}


}
