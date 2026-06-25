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
  protected SymTypeExpression calculatePlus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculatePlus(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculatePlus(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  /**
   * calculates {@code +,-,%}
   * without support for String
   */
  @Override
  protected SymTypeExpression calculatePlusMinusModulo(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculatePlusMinusModulo(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculatePlusMinusModulo(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculatePlusString(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculatePlusString(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculatePlusString(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  /**
   * See Documentation in super class
   */
  @Override
  protected SymTypeExpression calculateToString(
      SymTypeExpression inner
  ) {
    var type = super.calculateToString(inner);
    if(type.isObscureType() && inner.isFunctionType()){
      return super.calculateToString(getFunctionReturnType(inner));
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculateMultiply(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateMultiply(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateMultiply(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculateDivide(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateDivide(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateDivide(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  /**
   * calculates {@code +,-,*,/,%}
   * without support for String and SIUnits
   */
  @Override
  protected SymTypeExpression calculateArithmeticExpressionNumeric(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateArithmeticExpressionNumeric(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateArithmeticExpressionNumeric(
          getFunctionReturnType(left),
          getFunctionReturnType(right)
      );
    }
    return type;
  }

  /**
   * calculates unary {@code +,-}
   */
  @Override
  protected SymTypeExpression calculatePlusMinusPrefix(SymTypeExpression inner) {
    var type = super.calculatePlusMinusPrefix(inner);
    if(type.isObscureType() && inner.isFunctionType()){
      return super.calculatePlusMinusPrefix(getFunctionReturnType(inner));
    }
    return type;
  }

  /**
   * calculates {@code ==, !=}
   */
  @Override
  protected SymTypeExpression calculateEqualityInequality(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateEqualityInequality(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateEqualityInequality(
          getFunctionReturnType(left),
          getFunctionReturnType(right)
      );
    }
    return type;
  }

  /**
   * calculates {@code <, <=, >, =>}
   */
  @Override
  protected SymTypeExpression calculateNumericComparison(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateNumericComparison(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateNumericComparison(
          getFunctionReturnType(left),
          getFunctionReturnType(right)
      );
    }
    return type;
  }

  /**
   * calculates {@code &&, ||}
   */
  @Override
  protected SymTypeExpression calculateConditionalBooleanOp(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateConditionalBooleanOp(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateConditionalBooleanOp(
          getFunctionReturnType(left),
          getFunctionReturnType(right)
      );
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculateLogicalNot(SymTypeExpression inner) {
    SymTypeExpression unpacked = getFunctionReturnType(inner);
    var result = super.calculateLogicalNot(inner);
    var unpackedResult = super.calculateLogicalNot(getFunctionReturnType(inner));
    if (SymTypeRelations.isBoolean(result)) {
      return result;
    } else if (SymTypeRelations.isBoolean(unpackedResult)) {
      return unpackedResult;
    }
    // TODO resolve for Stream and deepEquals
    else if (inner.hasTypeInfo() && inner.getTypeInfo().isPresentSuperClass() &&
        inner.getTypeInfo().getSuperClass().print().equals("Stream<T>")) {
      return inner;
    }
    else if (unpacked.hasTypeInfo() &&
        unpacked.getTypeInfo().isPresentSuperClass() &&
        unpacked.getTypeInfo().getSuperClass().print().equals("Stream<T>")) {
      return unpacked;
    }

    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * calculates {@code &, |, ^}
   */
  @Override
  protected SymTypeExpression calculateBinaryInfixOp(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateBinaryInfixOp(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateBinaryInfixOp(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  /**
   * calculates ~
   */
  @Override
  protected SymTypeExpression calculateBitwiseComplement(SymTypeExpression inner) {
    var type = super.calculateBitwiseComplement(inner);
    if(type.isObscureType() && inner.isFunctionType()){
      return super.calculateBitwiseComplement(getFunctionReturnType(inner));
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculateShift(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateShift(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateShift(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
  }

  @Override
  protected SymTypeExpression calculateAssignment(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    var type = super.calculateAssignment(left, right);
    if(type.isObscureType() &&
      (left.isFunctionType() || right.isFunctionType())
    ){
      return super.calculateAssignment(getFunctionReturnType(left), getFunctionReturnType(right));
    }
    return type;
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
    var type = super.calculateLogicalNot(inner);
    if(type.isObscureType() && inner.isFunctionType()){
      return super.calculateLogicalNot(getFunctionReturnType(inner));
    }
    return type;
  }

  /**
   * Returns the ReturnType of a FunctionType.
   * TypeCheck3 returns the FunctionTypes signature (for "list.count()"
   * TC3 would return "() -> int").
   * So this is used to allow using functions directly with operators,
   * effectively treating them as their return value.
   */
  protected SymTypeExpression getFunctionReturnType(SymTypeExpression expression) {
    if (expression.isFunctionType()) {
      return expression.asFunctionType().getType();
    }
    return expression;
  }


}
