package de.monticore.lang.sysmlactions._symboltable;

import de.monticore.types.check.SymTypeExpression;

public class CalcUsageSymbol extends CalcUsageSymbolTOP {
  protected SymTypeExpression returnType;

  public CalcUsageSymbol(String name) {
    super(name);
  }

  public SymTypeExpression getReturnType() {
    return returnType;
  }

  public void setReturnType(SymTypeExpression returnType) {
    this.returnType = returnType;
  }
}
