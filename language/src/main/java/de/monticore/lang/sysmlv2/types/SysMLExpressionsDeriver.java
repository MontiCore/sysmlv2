package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlexpressions.SysMLExpressionsMill;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.DeriveSymTypeOfMCCommonLiterals;

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

    DeriveSymTypeOfMCCommonLiterals commonliterals = new DeriveSymTypeOfMCCommonLiterals();
    commonliterals.setTypeCheckResult(typeCheckResult);
    getTraverser().add4MCCommonLiterals(commonliterals);

    DeriveSymTypeOfExpression forBasisExpr = new DeriveSymTypeOfExpression();
    forBasisExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().add4ExpressionsBasis(forBasisExpr);
    getTraverser().setExpressionsBasisHandler(forBasisExpr);

    DeriveSymTypeOfCommonExpressions forCommonExpr = new DeriveSymTypeOfCommonExpressions();
    forCommonExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().add4CommonExpressions(forCommonExpr);
    getTraverser().setCommonExpressionsHandler(forCommonExpr);

    DeriveSymTypeOfOCLExpressions forOCLExpr = new DeriveSymTypeOfOCLExpressions();
    forOCLExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().setOCLExpressionsHandler(forOCLExpr);

    // TODO Ausbauen
    //  anschauen welche Grammatiken zu einer Exppression beitragen
    //  Stück für Stück für diese Grammatiken Visitoren/ Traversers bauen, die ihre Komponenten
    //  in Variable-Usage, Variable-Declaration, Member, Objekte transformieren auf denen
    //  dann ein Typ check durchgeführt wird.
  }

}
