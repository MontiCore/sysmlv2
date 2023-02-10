package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlbasis._ast.ASTDefaultValueBuilder;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlstates._symboltable.SysMLStatesScope;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;

public class StateVisitor implements SysMLStatesVisitor2 {

  @Override
  public void visit(ASTStateUsage node) {
    if(node.getSysMLElementList().stream().anyMatch(t -> t instanceof ASTStateUsage)) {
      node.setIsAutomaton(true);
      node.getSysMLElementList().add(createStateUsage("start", node.getSpannedScope()));
      node.getSysMLElementList().add(createStateUsage("done", node.getSpannedScope()));
    }
  }

  @Override
  public void visit(ASTStateDef node) {
    if(node.getSysMLElementList().stream().anyMatch(t -> t instanceof ASTStateUsage))
      node.setIsAutomaton(true);
    node.getSysMLElementList().add(createStateUsage("first", node.getSpannedScope()));
    node.getSysMLElementList().add(createStateUsage("done", node.getSpannedScope()));

  }

  ASTStateUsage createStateUsage(String name, ISysMLStatesScope enclosingScope) {
    ASTModifierBuilder modifierBuilder = new ASTModifierBuilder();
    ASTDefaultValueBuilder defaultValueBuilder = new ASTDefaultValueBuilder();
    StateUsageSymbol stateUsageSymbol = new StateUsageSymbol(name);
    ASTStateUsage stateUsage = new ASTStateUsage();
    stateUsageSymbol.setAstNode(stateUsage);
    stateUsage.setName(name);
    stateUsage.setEnclosingScope(enclosingScope);
    stateUsage.setSpannedScope(new SysMLStatesScope());
    stateUsage.setModifier(modifierBuilder.build());
    stateUsage.setDefaultValue(defaultValueBuilder.build());
    stateUsage.setSymbol(stateUsageSymbol);
    return stateUsage;
  }

}
