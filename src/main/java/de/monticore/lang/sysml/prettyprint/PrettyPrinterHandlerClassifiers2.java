package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.classifiers._ast.ASTSuperclassingList;
import de.monticore.lang.sysml.basics.classifiers._visitor.ClassifiersHandler;
import de.monticore.lang.sysml.basics.classifiers._visitor.ClassifiersTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerClassifiers2 implements ClassifiersHandler {
	private IndentPrinter printer;
	private ClassifiersTraverser traverser;

	public PrettyPrinterHandlerClassifiers2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}
	@Override
	public ClassifiersTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(ClassifiersTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTSuperclassingList node) {
		printer.print(node.getSpecializesKeyword());
		for (int i = 0; i < node.getQualifiedNameList().size(); i++) {
			getTraverser().handle(node.getQualifiedName(i));
			if (i + 1 < node.getQualifiedNameList().size()) {
				printer.print(", ");
			}
		}
	}
}
