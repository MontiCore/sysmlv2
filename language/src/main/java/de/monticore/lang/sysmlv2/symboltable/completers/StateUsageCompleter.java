package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;

import java.util.stream.Collectors;

public class StateUsageCompleter implements SysMLStatesVisitor2 {
  @Override
  public void visit(ASTStateUsage node) {
    if(node.isPresentSymbol()) {
      node.getSymbol().setExhibited(node.isExhibited());
      node.getSymbol().setUserDefinedKeywordsList(
          node.getUserDefinedKeywordList().stream()
              .map(key -> key.getMCQualifiedName().toString())
              .collect(Collectors.toList())
      );
    }
  }
}
