package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlexpressions._ast.ASTElementOfExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTInfinity;
import de.monticore.lang.sysmlexpressions._ast.ASTSubsetEquationExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLInstantiation;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.types.check.AbstractDeriveFromExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class SysMLExressionsDeriver extends AbstractDeriveFromExpression implements SysMLExpressionsVisitor2,
    SysMLExpressionsHandler {

  /* ########################## HÄSSLICHER BOILERPLATE CODE START ########################## */
  protected SysMLExpressionsTraverser traverser;

  protected SysMLSynthesizer synthesizer;

  public SysMLExressionsDeriver(SysMLExpressionsTraverser traverser, SysMLSynthesizer synthesizer) {
    this.traverser = traverser;
    this.synthesizer = synthesizer;
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

  @Override
  public void traverse(ASTSubsetEquationExpression node) {
    getTypeCheckResult().reset();
    node.getLeft().accept(getTraverser());
    TypeCheckResult lhs = getTypeCheckResult().copy();
    getTypeCheckResult().reset();
    node.getRight().accept(getTraverser());
    TypeCheckResult rhs = getTypeCheckResult().copy();

    var start = node.get_SourcePositionStart();
    var end = node.get_SourcePositionEnd();
    if(!lhs.isPresentResult()) {
      Log.error("0x81002 LHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.isPresentResult()) {
      Log.error("0x81003 RHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!lhs.getResult().getTypeInfo().getFullName().equals("Set")) {
      Log.error("0x81004 LHS was expected to be a set, but was " + lhs.getResult().printFullName(), start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.getResult().getTypeInfo().getFullName().equals("Set")) {
      Log.error("0x81005 RHS was expected to be a set, but was " + lhs.getResult().printFullName(), start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else {
      // TODO Inner types vergleichen / compatibility checken
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createPrimitive("boolean"));
    }
  }

  @Override
  public void traverse(ASTElementOfExpression node) {
    getTypeCheckResult().reset();
    node.getLeft().accept(getTraverser());
    TypeCheckResult lhs = getTypeCheckResult().copy();
    getTypeCheckResult().reset();
    node.getRight().accept(getTraverser());
    TypeCheckResult rhs = getTypeCheckResult().copy();

    var start = node.get_SourcePositionStart();
    var end = node.get_SourcePositionEnd();
    if(!lhs.isPresentResult()) {
      Log.error("0x81006 LHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.isPresentResult()) {
      Log.error("0x81007 RHS could not be calculated", start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else if(!rhs.getResult().getTypeInfo().getFullName().equals("Set")) {
      Log.error("0x81008 RHS was expected to be a set, but was " + lhs.getResult().printFullName(), start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    if(!((SymTypeOfGenerics)rhs.getResult()).getArgument(0).printFullName().equals(lhs.getResult().printFullName())) {
      Log.error("0x81009 LHS was expected to be compatible with elements of RHS, but was " + lhs.getResult().printFullName(),
          start, end);
      typeCheckResult.setResult(SymTypeExpressionFactory.createObscureType());
    }
    else {
      // TODO Inner types vergleichen / compatibility checken
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createPrimitive("boolean"));
    }
  }

  @Override
  public void traverse(ASTSysMLInstantiation node) {
    getTypeCheckResult().setResult(synthesizer.synthesizeType(node.getMCType()).getResult());
  }

}
