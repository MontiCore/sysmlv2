/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTNonPortStructureUsageMemberValueProperty;
import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTValueDeclaration;
import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTValuePropertyPackagedUsageMember;
import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTValueTypeDeclaration;
import de.monticore.lang.sysml.common.sysmlvaluetypes._visitor.SysMLValueTypesVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterValueTypes2 implements SysMLValueTypesVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterValueTypes2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberValueProperty node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isValue()) {
			printer.print("value ");
		}
	}

	@Override
	public void visit(ASTValueTypeDeclaration node) {
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("value type ");
	}

	@Override
  public void visit(ASTValuePropertyPackagedUsageMember node) {
    printer.println("");
    if (node.isAbstract()) {
      printer.print("abstract ");
    }
    printer.print("value ");
  }

	@Override
	public void visit(ASTValueDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		if (node.isValue()) {
			printer.print("value ");
		}
	}
}
