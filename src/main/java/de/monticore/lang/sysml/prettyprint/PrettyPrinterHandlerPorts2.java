package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.basics.classifiers._visitor.ClassifiersTraverser;
import de.monticore.lang.sysml.basics.ports._ast.*;
import de.monticore.lang.sysml.basics.ports._visitor.PortsHandler;
import de.monticore.lang.sysml.basics.ports._visitor.PortsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerPorts2 implements PortsHandler {
	private IndentPrinter printer;
	private PortsTraverser traverser;

	public PrettyPrinterHandlerPorts2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public PortsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(PortsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTConnectionUsage node) {
		printer.println("");
		getTraverser().handle(node.getUsageDeclaration());
		printer.print("connect ");
		getTraverser().handle(node.getConnectionPart());
		getTraverser().handle(node.getAssociationBlockBody());
	}

	@Override
	public void handle(ASTConnectionPart node) {
		printer.println("");
		getTraverser().handle(node.getOwnedFeatureMembership_compFrom());
		printer.print("to ");
		if (node.isPresentOwnedFeatureMembership_compTo()) {
			getTraverser().handle(node.getOwnedFeatureMembership_compTo());
		} else {
			printer.print("(");
			for (int i = 0; i < node.getConnectorEndMemberList().size(); i++) {
				getTraverser().handle(node.getConnectorEndMember(i));
				if (i + 1 < node.getConnectorEndMemberList().size()) {
					printer.print(",");
				}
			}
			printer.print(")");
		}
	}

	@Override
	public void handle(ASTInterfaceUsage node) {
		printer.println("");
		getTraverser().handle(node.getUsageDeclaration());
		printer.print("connect ");
		getTraverser().handle(node.getConnectionPart());
		getTraverser().handle(node.getInterfaceBody());
	}

	@Override
	public void handle(ASTConjugatePortTypePart node) {
		printer.println("");
		getTraverser().handle(node.getTypedByKeyword());
		printer.print("~ ");
		getTraverser().handle(node.getConjugatedPortTyping());
	}

	@Override
	public void handle(ASTBindingConnector node) {
		printer.println("");
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				getTraverser().handle(node.getTypePart());
			}
			printer.print("as ");
		}
		getTraverser().handle(node.getConnectorEndMember(0));
		printer.print("= ");
		getTraverser().handle(node.getConnectorEndMember(1));
		getTraverser().handle(node.getDefinitionBody());
	}

	@Override
	public void handle(ASTInterfaceEndMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isPort()) {
			printer.print("port ");
		}
		getTraverser().handle(node.getPortUsage());
	}

	@Override
	public void handle(ASTConjugatedInterfaceEndMember node) {
		printer.println("");
		getTraverser().handle(node.getDefinitionMemberPrefix());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isPort()) {
			printer.print("port ");
		}
		getTraverser().handle(node.getConjugatedPortUsage());
	}
}
