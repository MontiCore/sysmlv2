package de.monticore.lang.sysmlv2.types3;

import de.monticore.types.check.SymTypeExpression;


public class SysMLBuiltInTypeRelations extends de.monticore.types3.util.BuiltInTypeRelations{

  @Override
  public boolean isIntegralType(SymTypeExpression type) {
    return super.isIntegralType(type) ||
      isNat(type) ||
      checkFullName(type, "ScalarValues.Integer");
  }

  @Override
  public boolean isBoolean(SymTypeExpression type) {
    return super.isBoolean(type) || checkFullName(type, "ScalarValues.Boolean");
  }

  public boolean isNat(SymTypeExpression type) {
    return (
      type.isPrimitive() &&
      type.asPrimitive().getPrimitiveName().equals("nat")
    ) ||
      checkFullName(type, "ScalarValues.Natural") ||
      checkFullName(type, "ScalarValues.Positive");
  }

  private boolean checkFullName(SymTypeExpression type, String fullName) {
    return type.hasTypeInfo() &&
      type.getTypeInfo().getFullName().equals(fullName);
  }
}
