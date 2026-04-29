package de.monticore.lang.sysmlactions._symboltable;

import de.monticore.types.check.SymTypeExpression;

public class CalcDefSymbol extends CalcDefSymbolTOP {
  protected SymTypeExpression returnType;

  public CalcDefSymbol(String name) {
    super(name);
  }

  public SymTypeExpression getReturnType() {
    return returnType;
  }

  public void setReturnType(SymTypeExpression returnType) {
    this.returnType = returnType;
  }
}
