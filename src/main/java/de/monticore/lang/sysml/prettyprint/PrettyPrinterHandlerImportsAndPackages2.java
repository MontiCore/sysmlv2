package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.interfaces.commentsbasis._ast.ASTPrefixAnnotation;
import de.monticore.lang.sysml.basics.interfaces.importbasis._ast.ASTImportUnit;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageBody;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageMember;
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
		if (!node.isEmptyPrefixAnnotations()) {
			for (ASTPrefixAnnotation p :
				node.getPrefixAnnotationList()) {
				p.accept(getTraverser());
			}
		}
		if (node.isPresentVisibility()) {
			node.getVisibility().accept(getTraverser());
		}
		printer.print("import ");
		node.getQualifiedName().accept(getTraverser());
		String s = printer.getContent().trim();
		printer.clearBuffer();
		printer.print(s);
		switch (node.getStar()) {
			case 1:
				printer.print("::* ");
				break;
			case 2:
				printer.print(".* ");
				break;
		}
		if (node.isPresentSysMLName()) {
			printer.print("as " + node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
		printer.print(";");
	}

	@Override
	public void handle(ASTPackageBody node) {
		printer.println("{");
		printer.indent();

		for (ASTImportUnit i:
				 node.getImportUnitList()) {
			i.accept(getTraverser());
		}
		printer.println("");
		for (ASTPackageMember p:
				 node.getPackageMemberList()){
			p.accept(getTraverser());
		}

		printer.println("");
		printer.unindent();
		printer.println("}");
	}
}
