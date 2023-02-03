package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;

public class StateVisitor implements SysMLStatesVisitor2 {

  @Override
  public void visit(ASTStateUsage node) {
    if(node.getSysMLElementList().stream().anyMatch(t -> t instanceof ASTStateUsage))
      node.setIsAutomaton(true);

  }

  @Override
  public void visit(ASTStateDef node) {
    if(node.getSysMLElementList().stream().anyMatch(t -> t instanceof ASTStateUsage))
      node.setIsAutomaton(true);

  }

}
