package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.commentsbasis._ast.ASTPrefixAnnotation;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._visitor.ImportsAndPackagesHandler;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._visitor.ImportsAndPackagesTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerImportsAndPackages2 implements ImportsAndPackagesHandler {
	private IndentPrinter printer;
	private ImportsAndPackagesTraverser traverser;

	public PrettyPrinterHandlerImportsAndPackages2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public ImportsAndPackagesTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(ImportsAndPackagesTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTImportUnitStd node) {
		printer.println("");
		if (node.isEmptyPrefixAnnotations()) {
			for (ASTPrefixAnnotation p :
				node.getPrefixAnnotationList()) {
				getTraverser().handle(p);
			}
		}
		if (node.isPresentVisibility()) {
			getTraverser().handle(node.getVisibility());
		}
		printer.print("import ");
		getTraverser().handle(node.getQualifiedName());
		switch (node.getStar()) {
			case 1:
				printer.print("::*");
				break;
			case 2:
				printer.print(".*");
				break;
		}
		if (node.isPresentSysMLName()) {
			printer.print("as " + node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
		printer.print(";");
	}
}
