package de.monticore.lang.sysmlv2._prettyprint;

import de.monticore.lang.sysmlv2._ast.ASTSysMLQualifiedName;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class SysMLv2PrettyPrinter extends SysMLv2PrettyPrinterTOP {

  public SysMLv2PrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  // Fix pretty-print of SysMLQualifiedName with the intent of reproducing impl. of MCQualifiedName
  @Override
  public void handle(ASTSysMLQualifiedName node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Iterator<String> iterName = node.getNameList().iterator();
    if (iterName.hasNext()) {
      getPrinter().print(iterName.next());
      while (iterName.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(".");
        getPrinter().print(iterName.next());
      }
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
