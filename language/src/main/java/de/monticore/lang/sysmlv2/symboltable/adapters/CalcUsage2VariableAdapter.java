package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.sysmlactions._symboltable.CalcUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

public class CalcUsage2VariableAdapter extends VariableSymbol {
  private final CalcUsageSymbol adaptee;

  public CalcUsage2VariableAdapter(CalcUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  public CalcUsageSymbol getAdaptee() {
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
  public SymTypeExpression getType() {
    return SymTypeExpressionFactory.createFunction(
        getAdaptee().getReturnType(),
        getAdaptee().getArgTypes()
    );
  }

}
