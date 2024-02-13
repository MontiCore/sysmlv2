package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.automaton._ast.ASTStateSpace;
import de.monticore.lang.automaton._symboltable.AutomatonSymbol;
import de.monticore.lang.automaton._symboltable.IAutomatonScope;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;

public class StateUsage2AutomatonAdapter extends AutomatonSymbol {
  private final StateUsageSymbol adaptee;

  private final PartDefSymbol container;

  public StateUsage2AutomatonAdapter(PartDefSymbol container, StateUsageSymbol adaptee) {
    super(container.getName());
    this.container = container;
    this.adaptee = adaptee;
  }

  public StateUsageSymbol getAdaptee() {
    return adaptee;
  }

  public PartDefSymbol getContainer() {
    return container;
  }

  @Override
  public String getFullName() {
    return getContainer().getFullName();
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) getAdaptee().getEnclosingScope();
  }

  @Override
  public ASTStateSpace getStateSpace() {
    return new StateSpaceWrapper(getAdaptee());
  }
}
