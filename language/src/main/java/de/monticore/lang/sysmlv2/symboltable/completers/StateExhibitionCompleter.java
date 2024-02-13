package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;

public class StateExhibitionCompleter implements SysMLStatesVisitor2 {
  @Override
  public void visit(ASTStateUsage node) {
    if(node.isPresentSymbol()) {
      node.getSymbol().setExhibited(node.isExhibited());
    }
  }
}
