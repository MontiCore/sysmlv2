package de.monticore.lang.sysmlv2.types3;

import de.monticore.types.check.SymTypeExpression;

/**
 * Dient dazu die MontiCore Built-Ins (int, String, boolean, nat) mit den
 * KerML-ScalarValues (Integer, String, Boolean, Natural/Positive) kompatibel
 * zu machen.
 */
public class SysMLBuiltInTypeRelations extends de.monticore.types3.util.BuiltInTypeRelations{

  @Override
  public boolean isIntegralType(SymTypeExpression type) {
    return super.isIntegralType(type)
        || (type.isPrimitive()
          && type.asPrimitive().getPrimitiveName().equals("nat"))
        || (type.hasTypeInfo()
          && (
            type.getTypeInfo().getFullName().equals("ScalarValues.Integer") ||
            type.getTypeInfo().getFullName().equals("ScalarValues.Natural") ||
            type.getTypeInfo().getFullName().equals("ScalarValues.Positive"))
    );
  }

  @Override
  public boolean isBoolean(SymTypeExpression type) {
    return super.isBoolean(type) ||
      (type.hasTypeInfo() &&
        type.getTypeInfo().getFullName().equals("ScalarValues.Boolean"));
  }
}
