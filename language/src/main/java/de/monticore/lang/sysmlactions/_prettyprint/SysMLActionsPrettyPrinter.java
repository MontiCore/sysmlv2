/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlactions._prettyprint;

import de.monticore.lang.sysmlactions._ast.ASTSuccessionThen;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class SysMLActionsPrettyPrinter extends SysMLActionsPrettyPrinterTOP {

  public SysMLActionsPrettyPrinter(IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTSuccessionThen node) {

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentMCQualifiedName()) {
      node.getMCQualifiedName().accept(getTraverser());

      if (node.isPresentSysMLCardinality()) {
        node.getSysMLCardinality().accept(getTraverser());
      }

      for (ASTSpecialization spec : node.getSpecializationList()) {
        spec.accept(getTraverser());
      }

      getPrinter().print(" { ");
      for (ASTSysMLElement e : node.getSysMLElementList()) {
        e.accept(getTraverser());
      }
      getPrinter().print(" } ");

    }
    else if (node.sizeSysMLElements() == 1) {
      node.getSysMLElement(0).accept(getTraverser());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}


