package de.monticore.lang.sysmlparts.symboltable.completers;

import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

public class SysMLPartsCompleter implements SysMLPartsVisitor2 {

  public void visit(ASTPortUsage node) {
    node.getSymbol().setHasCardinality(node.getCardinality().isPresent());
  }
}
