/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.lang.sysmlexpressions.SysMLExpressionsMill;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;

public class SysMLDeriver extends AbstractDerive {
  /**
   * <p>{@code isStream} is used to determine whether the type of the expression is calculated as a Stream,
   * it is initialized in the constructor and as a parameter when {@link SysMLv2DeriveSymTypeOfCommonExpressions}
   * is instantiated.</p>
   * @see SysMLv2DeriveSymTypeOfCommonExpressions#calculateFieldAccess(ASTFieldAccessExpression, boolean)
   */
  protected boolean isStream;

  /**
   * For constructor without parameter, we set isStream default as true,
   * because it involves some previous unit tests,
   * the type of AttributeUsageSymbol was previously set to Stream by default.
   */
  public SysMLDeriver() {
    super(SysMLv2Mill.traverser());
    this.isStream = true;
    init();
  }

  public SysMLDeriver(boolean isStream) {
    super(SysMLExpressionsMill.traverser());
    this.isStream = isStream;
    init();
  }

  @Override public SysMLv2Traverser getTraverser() {
    return (SysMLv2Traverser) traverser;
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

    SysMLv2DeriveSymTypeOfCommonExpressions forCommonExpr = new SysMLv2DeriveSymTypeOfCommonExpressions(this.isStream);
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

    var synthesizer = new SysMLSynthesizer();
    synthesizer.init(getTraverser());

    var deriverForSysMLExpressions = new SysMLExressionsDeriver(getTraverser(), synthesizer);
    deriverForSysMLExpressions.setTypeCheckResult(typeCheckResult);
    getTraverser().add4SysMLExpressions(deriverForSysMLExpressions);
    getTraverser().setSysMLExpressionsHandler(deriverForSysMLExpressions);
  }
}
