package de.monticore.lang.sysmlactions._symboltable;

import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class CalcUsageSymbol extends CalcUsageSymbolTOP {

  protected List<SymTypeExpression> argTypes;

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

  public List<SymTypeExpression> getArgTypes() {
    return argTypes;
  }

  public void setArgTypes(List<SymTypeExpression> argTypes) {
    this.argTypes = argTypes;
  }
}
