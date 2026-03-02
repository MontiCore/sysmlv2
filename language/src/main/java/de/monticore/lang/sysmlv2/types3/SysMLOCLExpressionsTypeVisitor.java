package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.sysmlexpressions._ast.ASTConditionalAndExpression2;
import de.monticore.lang.sysmlexpressions._ast.ASTExistsExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorLifting;
import de.se_rwth.commons.logging.Log;

public class SysMLOCLExpressionsTypeVisitor extends OCLExpressionsTypeVisitor implements
    SysMLExpressionsVisitor2 {
  @Override
  public void endVisit(ASTExistsExpression node) {
    endVisit((de.monticore.ocl.oclexpressions._ast.ASTExistsExpression) node);
  }

  @Override
  public void endVisit(ASTConditionalAndExpression2 expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                this::calculateAndExpression)
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateAndExpression(
      SymTypeExpression left, SymTypeExpression right) {

    if (SymTypeRelations.isCompatible(left, right)
        && left.isPrimitive()
        && left.asPrimitive().getPrimitiveName().equals("boolean")) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return SymTypeExpressionFactory.createObscureType();
  }

}
