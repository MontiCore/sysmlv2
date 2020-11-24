package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.ports._ast.*;
import de.monticore.lang.sysml.basics.ports._visitor.PortsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterPorts2 implements PortsVisitor2 {
	private IndentPrinter printer;

	public PrettyPrinterPorts2(IndentPrinter print) {
		this.printer = print;
	}

	@Override
	public void visit(ASTPortDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("port def ");
	}

	@Override
	public void visit(ASTConjugatePortUsageDeclaration node) {
		printer.println("");
		if (node.isPresentSysMLName()) {
			printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
		}
	}

	@Override
	public void visit(ASTConjugatedPortTyping node) {
		printer.print(node.getSysMLName().getNameForPrettyPrinting()+" ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberConnectionUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("link ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberConnector node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("connect ");
	}

	@Override
	public void visit(ASTConnectorEndMember node) {
		printer.println("");
		if (node.isPresentMemberName()) {
			printer.print(node.getMemberName().getNameForPrettyPrinting() + " => ");
		}
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberInterfaceUsage node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("interface ");
	}

	@Override
	public void visit(ASTNonPortStructureUsageMemberBindingConnector node) {
		printer.println("");
		printer.print("bind ");
	}

	@Override
	public void visit(ASTInterfaceDeclaration node) {
		printer.println("");
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("interface def ");
	}

	@Override
	public void visit(ASTInterfaceBody node) {
		if (node.isEmptyImportUnits() && node.isEmptyInterfaceUsageMembers() && node.isEmptyNestedDefinitionMembers()) {
			printer.print(";");
		} else {
			printer.println("");
			printer.indent();
			printer.println("{");
		}
	}

	@Override
	public void endVisit(ASTInterfaceBody node) {
		if (!(node.isEmptyImportUnits() && node.isEmptyInterfaceUsageMembers() && node.isEmptyNestedDefinitionMembers())) {
			printer.println("");
			printer.unindent();
			printer.println("}");
		}
	}
}
