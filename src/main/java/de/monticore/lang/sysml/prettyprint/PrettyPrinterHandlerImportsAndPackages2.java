package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmlcommentsbasis._ast.ASTPrefixAnnotation;
import de.monticore.lang.sysml.basics.sysmlimportbasis._ast.ASTImportUnit;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTPackageBody;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTPackageMember;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesHandler;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerImportsAndPackages2 implements SysMLImportsAndPackagesHandler {
	private IndentPrinter printer;
	private SysMLImportsAndPackagesTraverser traverser;

	public PrettyPrinterHandlerImportsAndPackages2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLImportsAndPackagesTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLImportsAndPackagesTraverser realThis) {
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
    if (node.isStar()) {
      printer.print(".* ");
    }
		printer.print(";");
	}
	
	@Override
	public void handle(ASTAliasPackagedDefinitionMember node) {
	  if (node.isAlias()) {
	    printer.print("alias ");
	  } else {
	    printer.print("import ");
	  }
    node.getQualifiedName().accept(getTraverser());
    String s = printer.getContent().trim();
    printer.clearBuffer();
    printer.print(s);
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
