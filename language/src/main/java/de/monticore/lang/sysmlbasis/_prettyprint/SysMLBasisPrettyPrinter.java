package de.monticore.lang.sysmlbasis._prettyprint;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedName;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class SysMLBasisPrettyPrinter extends SysMLBasisPrettyPrinterTOP {

  public SysMLBasisPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  // Fix pretty-print of SysMLQualifiedName with the intent of reproducing impl. of MCQualifiedName
  @Override
  public  void handle (ASTSysMLQualifiedName node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<String> iter_name = node.getNameList().iterator();
    if (iter_name.hasNext()) {
      getPrinter().print(iter_name.next());
      while (iter_name.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(".");
        getPrinter().print(iter_name.next());
      }
    }
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
