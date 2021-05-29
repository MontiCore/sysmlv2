/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlcomments._ast.ASTComment;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlcomments._ast.ASTDocumentation;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlcomments._ast.ASTSysMLAnnotation;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlcomments._visitor.SysMLCommentsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterComments2 implements SysMLCommentsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterComments2(IndentPrinter print) {
		this.printer = print;
	}
	@Override
	public void visit(ASTComment node) {
		printer.println();
		printer.print("comment ");
		if(node.isPresentSysMLName()){
			printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
		}
	}

	@Override
	public void visit(ASTSysMLAnnotation node) {
		printer.print("about ");
	}

	@Override
	public void visit(ASTDocumentation node) {
		printer.println();
		printer.print("comment ");
		if(node.isPresentSysMLName()){
			printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
		}
	}
}
