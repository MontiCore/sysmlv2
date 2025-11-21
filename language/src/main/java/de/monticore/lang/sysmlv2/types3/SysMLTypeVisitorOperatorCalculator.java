package de.monticore.lang.sysmlv2.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;

public class SysMLTypeVisitorOperatorCalculator extends
    TypeVisitorOperatorCalculator {

  public static void init() {
    // TODO might be wrong
    setDelegate(new SysMLTypeVisitorOperatorCalculator());
  }

  @Override
  protected SymTypeExpression calculateLogicalNot(SymTypeExpression inner) {
    var result = super.calculateLogicalNot(inner);
    if (SymTypeRelations.isBoolean(result)) {
      return result;
    }
    else if (inner.hasTypeInfo() && inner.printFullName().contains("Stream")) {
      return inner;
    }

    return SymTypeExpressionFactory.createObscureType();
  }
}
