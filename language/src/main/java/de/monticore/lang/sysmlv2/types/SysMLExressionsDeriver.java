package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlexpressions._ast.ASTInfinity;
import de.monticore.lang.sysmlexpressions._ast.ASTSubsetEquationExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.types.check.AbstractDeriveFromExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class SysMLExressionsDeriver extends AbstractDeriveFromExpression implements SysMLExpressionsVisitor2,
    SysMLExpressionsHandler {

  /* ########################## HÄSSLICHER BOILERPLATE CODE START ########################## */
  protected SysMLExpressionsTraverser traverser;

  public SysMLExressionsDeriver(SysMLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public SysMLExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SysMLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  /* ########################## HÄSSLICHER BOILERPLATE CODE ENDE ########################### */

  @Override
  public void visit(ASTInfinity node) {
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createPrimitive("int"));
  }

  protected TypeCheckResult lhs;
  protected TypeCheckResult rhs;

  @Override
  public void traverse(ASTSubsetEquationExpression node) {
    getTypeCheckResult().reset();
    node.getLeft().accept(getTraverser());
    lhs = getTypeCheckResult().copy();
    getTypeCheckResult().reset();
    node.getRight().accept(getTraverser());
    rhs = getTypeCheckResult().copy();
  }

  @Override
  public void endVisit(ASTSubsetEquationExpression node) {
    var start = node.get_SourcePositionStart();
    var end = node.get_SourcePositionEnd();
    if(!lhs.isPresentResult()) {
      Log.error("LHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.isPresentResult()) {
      Log.error("RHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!lhs.getResult().getTypeInfo().getFullName().equals("Set")) {
      Log.error("LHS was expected to be a set, but was " + lhs.getResult().printFullName(), start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.getResult().getTypeInfo().getFullName().equals("Set")) {
      Log.error("RHS was expected to be a set, but was " + lhs.getResult().printFullName(), start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else {
      // TODO Inner types vergleichen / compatibility checken
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createPrimitive("boolean"));
    }
  }

}
