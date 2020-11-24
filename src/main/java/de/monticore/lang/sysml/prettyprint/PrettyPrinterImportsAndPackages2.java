package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageBody;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageDeclaration;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._visitor.ImportsAndPackagesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterImportsAndPackages2 implements ImportsAndPackagesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterImportsAndPackages2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTAliasPackagedDefinitionMember node) {
		printer.println("");
		printer.print("import ");
	}

	@Override
	public void endVisit(ASTAliasPackagedDefinitionMember node) {
		if (node.isPresentSysMLName()) {
			printer.print("as " + node.getSysMLName().getNameForPrettyPrinting());
		}
	}

	@Override
	public void visit(ASTPackageDeclaration node) {
		printer.println("package ");
	}

	@Override
	public void visit(ASTPackageBody node) {
		printer.println("");
		printer.print("{");
		printer.indent();
	}

	@Override
	public void endVisit(ASTPackageBody node) {
		printer.println("");
		printer.unindent();
		printer.println("}");
	}
}
