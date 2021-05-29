/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.prettyprint;

import de.monticore.lang.sysml.common.sysmlclassifiers._visitor.SysMLClassifiersTraverser;
import de.monticore.lang.sysml.common.sysmlports._ast.*;
import de.monticore.lang.sysml.common.sysmlports._visitor.SysMLPortsHandler;
import de.monticore.lang.sysml.common.sysmlports._visitor.SysMLPortsTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class PrettyPrinterHandlerPorts2 implements SysMLPortsHandler {
	private IndentPrinter printer;
	private SysMLPortsTraverser traverser;

	public PrettyPrinterHandlerPorts2(IndentPrinter print, SysMLTraverser traverser) {
		this.printer = print;
		this.traverser = traverser;
	}

	@Override
	public SysMLPortsTraverser getTraverser() {
		return this.traverser;
	}

	@Override
	public void setTraverser(SysMLPortsTraverser realThis) {
		this.traverser = realThis;
	}

	@Override
	public void handle(ASTConnectionUsage node) {
		node.getUsageDeclaration().accept(getTraverser());
		printer.print("connect ");
		node.getConnectionPart().accept(getTraverser());
		node.getAssociationBlockBody().accept(getTraverser());
	}

	@Override
	public void handle(ASTConnectionPart node) {
		node.getOwnedFeatureMembership_compFrom().accept(getTraverser());
		printer.print("to ");
		if (node.isPresentOwnedFeatureMembership_compTo()) {
			node.getOwnedFeatureMembership_compTo().accept(getTraverser());
		} else {
			printer.print("(");
			for (int i = 0; i < node.getConnectorEndMemberList().size(); i++) {
				node.getConnectorEndMember(i).accept(getTraverser());
				if (i + 1 < node.getConnectorEndMemberList().size()) {
					printer.print(",");
				}
			}
			printer.print(") ");
		}
	}

	@Override
	public void handle(ASTInterfaceUsage node) {
		node.getUsageDeclaration().accept(getTraverser());
		printer.print("connect ");
		node.getConnectionPart().accept(getTraverser());
		node.getInterfaceBody().accept(getTraverser());
	}

	@Override
	public void handle(ASTConjugatePortTypePart node) {
		node.getTypedByKeyword().accept(getTraverser());
		printer.print("~ ");
		node.getConjugatedPortTyping().accept(getTraverser());
	}

	@Override
	public void handle(ASTBindingConnector node) {
		if (node.isPresentSysMLName() || node.isPresentTypePart()) {
			if (node.isPresentSysMLName()) {
				printer.print(node.getSysMLName().getNameForPrettyPrinting() + " ");
			}
			if (node.isPresentTypePart()) {
				node.getTypePart().accept(getTraverser());
			}
			printer.print("as ");
		}
		node.getConnectorEndMember(0).accept(getTraverser());
		printer.print("= ");
		node.getConnectorEndMember(1).accept(getTraverser());
		node.getDefinitionBody().accept(getTraverser());
	}

	@Override
	public void handle(ASTInterfaceEndMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isPort()) {
			printer.print("port ");
		}
		node.getPortUsage().accept(getTraverser());
	}

	@Override
	public void handle(ASTConjugatedInterfaceEndMember node) {
		printer.println("");
		node.getDefinitionMemberPrefix().accept(getTraverser());
		if (node.isAbstract()) {
			printer.print("abstract ");
		}
		printer.print("end ");
		if (node.isPort()) {
			printer.print("port ");
		}
		node.getConjugatedPortUsage().accept(getTraverser());
	}
	
	@Override
  public void handle(ASTPortMember node) {
    printer.println("");
    node.getDefinitionMemberPrefix().accept(getTraverser());
    if (node.isAbstract()) {
      printer.print("abstract ");
    }
    printer.print("port ");
    node.getPortUsage().accept(getTraverser());
  }

  @Override
  public void handle(ASTConjugatedPortMember node) {
    printer.println("");
    node.getDefinitionMemberPrefix().accept(getTraverser());
    if (node.isAbstract()) {
      printer.print("abstract ");
    }
    printer.print("port ");
    node.getConjugatedPortUsage().accept(getTraverser());
  }
  
  @Override
  public void handle(ASTConjugatedPortTyping node) {
    node.getQualifiedName().accept(getTraverser());
    String s = printer.getContent().trim();
    printer.clearBuffer();
    printer.print(s + " ");
  }
}
