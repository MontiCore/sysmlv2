package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.scbasis._symboltable.SCStateSymbol;

public class StateUsage2SCStateAdapter extends SCStateSymbol {

  protected StateUsageSymbol adaptee;

  public StateUsage2SCStateAdapter(StateUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  public StateUsageSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) getAdaptee().getEnclosingScope();
  }

  @Override
  public String getName() {
    return getAdaptee().getName();
  }
}
