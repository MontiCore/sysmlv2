package de.monticore.lang.sysmlv2.typecheck;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.types.check.*;

import java.util.Optional;

public class DeriveSysMLTypes implements IDerive {

  TypeCheckResult typeCheckResult = new TypeCheckResult();

  private SysMLv2Traverser traverser;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfSysMLExpression deriveSymTypeOfSysMLExpression;

  private DeriveSymTypeofSysMLBasis deriveSymTypeofSysMLBasis;

  public DeriveSysMLTypes() {
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if (typeCheckResult.isPresentCurrentResult()) {
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }

  @Override
  public void init() {
    this.traverser = SysMLv2Mill.traverser();
    this.typeCheckResult = new TypeCheckResult();

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfSysMLCommonExpressions();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfSysMLExpression = new DeriveSymTypeOfSysMLExpression();
    deriveSymTypeofSysMLBasis = new DeriveSymTypeofSysMLBasis();

    setTypeCheckResult(typeCheckResult);

    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4ExpressionsBasis(deriveSymTypeOfSysMLExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfSysMLExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
    traverser.add4SysMLExpressions(deriveSymTypeOfSysMLExpression);
    traverser.setSysMLExpressionsHandler(deriveSymTypeOfSysMLExpression);
    traverser.add4SysMLBasis(deriveSymTypeofSysMLBasis);
    traverser.setSysMLBasisHandler(deriveSymTypeofSysMLBasis);
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSysMLExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeofSysMLBasis.setTypeCheckResult(typeCheckResult);
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }
}
