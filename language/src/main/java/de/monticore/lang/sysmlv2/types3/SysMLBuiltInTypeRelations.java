package de.monticore.lang.sysmlv2.types3;

import de.monticore.types.check.SymTypeExpression;

/**
 * Dient dazu die MontiCore Built-Ins (int, String, boolean, nat) mit den
 * KerML-ScalarValues (Integer, String, Boolean, Natural/Positive) kompatibel
 * zu machen.
 */
public class SysMLBuiltInTypeRelations extends de.monticore.types3.util.BuiltInTypeRelations{

  @Override
  public boolean isBoolean(SymTypeExpression type) {
    return super.isBoolean(type) ||
      (type.hasTypeInfo() &&
        type.getTypeInfo().getFullName().equals("ScalarValues.Boolean"));
  }

  @Override
  public boolean isDouble(SymTypeExpression type) {
    return super.isDouble(type) ||
    (type.isObjectType() && (
      type.printFullName().equals("ScalarValues.Real") ||
      type.printFullName().equals("ScalarValues.Rational")
    ));
  }

  @Override
  public boolean isInt(SymTypeExpression type) {
    return super.isInt(type) || isNat(type) ||
      (type.isObjectType() &&
        type.printFullName().equals("ScalarValues.Integer"));
  }

  @Override
  public boolean isString(SymTypeExpression type) {
    return super.isString(type) || (type.isObjectType() &&
      type.printFullName().equals("ScalarValues.String"));
  }

  public boolean isNat(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals("nat");
    } else {
    return (type.isObjectType() && (
      type.printFullName().equals("ScalarValues.Natural") ||
      type.printFullName().equals("ScalarValues.Positive")));
    }
  }
}
