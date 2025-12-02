package de.monticore.lang.sysmlv2.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SysMLTypeVisitorOperatorCalculator extends
    TypeVisitorOperatorCalculator {

  public static void init() {
    Log.trace("init SysMLTypeVisitorOperatorCalculator", "TypeCheck setup");
    setDelegate(new SysMLTypeVisitorOperatorCalculator());
  }

  @Override
  protected SymTypeExpression calculateLogicalNot(SymTypeExpression inner) {
    var result = super.calculateLogicalNot(inner);
    if (SymTypeRelations.isBoolean(result)) {
      return result;
    }
    // TODO resolve for Stream and deepEquals
    else if (inner.hasTypeInfo() && inner.getTypeInfo().isPresentSuperClass() &&
        inner.getTypeInfo().getSuperClass().print().equals("Stream<T>")) {
      return inner;
    }

    return SymTypeExpressionFactory.createObscureType();
  }

  public static Optional<SymTypeExpression> conditionalNot(SymTypeExpression inner) {
    return ((SysMLTypeVisitorOperatorCalculator)getDelegate())._conditionalNot(inner);
  }

  protected Optional<SymTypeExpression> _conditionalNot(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateConditionalNot)
            .apply(inner);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateConditionalNot(SymTypeExpression inner) {
    return super.calculateLogicalNot(inner);
  }
}
