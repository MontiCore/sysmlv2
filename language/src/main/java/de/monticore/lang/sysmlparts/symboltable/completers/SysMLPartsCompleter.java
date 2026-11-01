package de.monticore.lang.sysmlparts.symboltable.completers;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

import java.util.stream.Collectors;

public class SysMLPartsCompleter implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    node.getSymbol().setUserDefinedKeywordsList(
        node.getUserDefinedKeywordList().stream()
            .map(key -> key.getMCQualifiedName().toString())
            .collect(Collectors.toList())
    );
  }

  public void visit(ASTPortUsage node) {
    node.getSymbol().setHasCardinality(node.getCardinality().isPresent());
  }
}
