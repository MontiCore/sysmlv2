/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlstates.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlstates._symboltable.StateDefSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;

public class StateDef2TypeSymbolAdapter extends TypeSymbol {
  protected StateDefSymbol adaptee;

  public StateDef2TypeSymbolAdapter(StateDefSymbol adaptee){
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
}

  protected StateDefSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public void setName(String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.getAdaptee().setName(name);
  }

  @Override
  public String getName() {
    return getAdaptee().getName();
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public IBasicSymbolsScope getSpannedScope() {
    return getAdaptee().getSpannedScope();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return getAdaptee().getSourcePosition();
  }

  // TODO needed?
  public StateDef2TypeSymbolAdapter deepClone() {
    StateDef2TypeSymbolAdapter clone = new StateDef2TypeSymbolAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    return clone;
  }
}
