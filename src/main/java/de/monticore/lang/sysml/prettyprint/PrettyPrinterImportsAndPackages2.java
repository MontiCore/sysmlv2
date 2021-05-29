/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.*;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterImportsAndPackages2 implements SysMLImportsAndPackagesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterImportsAndPackages2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTAliasPackagedDefinitionMember node) {
		printer.print("import ");
	}

	@Override
	public void endVisit(ASTAliasPackagedDefinitionMember node) {
		if (node.isPresentSysMLName()) {
			printer.print("as " + node.getSysMLName().getNameForPrettyPrinting());
		}
		printer.print(";");
	}

	@Override
	public void visit(ASTPackageDeclaration node) {
		printer.print("package ");
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}

	@Override
	public void visit(ASTPackageMember node) {
		printer.println("");
	}
}
