package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.Optional;

public class SysMLDeriveSymTypeOfExpression extends DeriveSymTypeOfExpression {

  @Override
  public void traverse(ASTNameExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateNameExpression(expr);
    if(wholeResult.isPresent()){
      getTypeCheckResult().setResult(wholeResult.get());
    }else{
     getTypeCheckResult().reset();
     getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }
}

