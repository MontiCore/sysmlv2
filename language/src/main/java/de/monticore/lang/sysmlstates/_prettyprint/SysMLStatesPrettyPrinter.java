/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlstates._prettyprint;

import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTEntryAction;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class SysMLStatesPrettyPrinter extends SysMLStatesPrettyPrinterTOP {
  public SysMLStatesPrettyPrinter(IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }

  /**
   * Bugfix for generated pretty printer.
   *
   * @param node
   */
  @Override
  public void handle(ASTEntryAction node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("entry ");
    if (node.isPresentMCQualifiedName()) {
      node.getMCQualifiedName().accept(getTraverser());

      getPrinter().println("{ ");

      getPrinter().indent();
      node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));
      getPrinter().unindent();

      getPrinter().println();
      getPrinter().println("} ");
    }
    else if (node.isPresentActionUsage()) {
      node.getActionUsage().accept(getTraverser());
    }
    else {
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(ASTDoAction node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("do ");

    if (node.isPresentActionUsage()) {
      node.getActionUsage().accept(getTraverser());
    } else if (node.isPresentMCQualifiedName()) {
      node.getMCQualifiedName().accept(getTraverser());
      getPrinter().println(" { ");
      node.getSysMLElementList().forEach(n -> n.accept(getTraverser()));
      getPrinter().println(" } ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
