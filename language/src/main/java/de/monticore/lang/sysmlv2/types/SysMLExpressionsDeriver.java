/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlexpressions.SysMLExpressionsMill;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;

public class SysMLExpressionsDeriver extends AbstractDerive {

  public SysMLExpressionsDeriver() {
    super(SysMLExpressionsMill.traverser());
    init();
  }

  @Override public SysMLExpressionsTraverser getTraverser() {
    return (SysMLExpressionsTraverser) traverser;
  }

  public void init() {
    DeriveSymTypeOfLiterals forLiterals = new DeriveSymTypeOfLiterals();
    forLiterals.setTypeCheckResult(typeCheckResult);
    getTraverser().add4MCLiteralsBasis(forLiterals);

    DeriveSymTypeOfMCCommonLiterals commonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    commonLiterals.setTypeCheckResult(typeCheckResult);
    getTraverser().add4MCCommonLiterals(commonLiterals);

    DeriveSymTypeOfExpression forBasisExpr = new DeriveSymTypeOfExpression();
    forBasisExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().add4ExpressionsBasis(forBasisExpr);
    getTraverser().setExpressionsBasisHandler(forBasisExpr);

    DeriveSymTypeOfCommonExpressions forCommonExpr = new DeriveSymTypeOfCommonExpressions();
    forCommonExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().add4CommonExpressions(forCommonExpr);
    getTraverser().setCommonExpressionsHandler(forCommonExpr);

    DeriveSymTypeOfSetExpressions deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfSetExpressions.setTypeCheckResult(typeCheckResult);
    getTraverser().add4SetExpressions(deriveSymTypeOfSetExpressions);
    getTraverser().setSetExpressionsHandler(deriveSymTypeOfSetExpressions);

    DeriveSymTypeOfOCLExpressions deriveSymTypeOfOCLExpressions = new DeriveSymTypeOfOCLExpressions();
    deriveSymTypeOfOCLExpressions.setTypeCheckResult(typeCheckResult);
    getTraverser().setOCLExpressionsHandler(deriveSymTypeOfOCLExpressions);

    SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    getTraverser().add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    getTraverser().setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);
  }

}
