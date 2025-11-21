package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsHandler;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsVisitor2;
import de.monticore.types.check.AbstractDeriveFromExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types3.streams.StreamSymTypeFactory;
import de.se_rwth.commons.logging.Log;

import java.util.List;

@Deprecated
public class SysMLDeriveSymTypeOfStreamExpressions extends AbstractDeriveFromExpression implements
    StreamExpressionsVisitor2, StreamExpressionsHandler {

  protected StreamExpressionsTraverser traverser;


  @Override
  public StreamExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(StreamExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTStreamConstructorExpression node) {
    getTypeCheckResult().reset();
    var first = node.getExpressionList().get(0);
    first.accept(getTraverser());
    TypeCheckResult fValue = getTypeCheckResult().copy();
    List<ASTExpression> eList = node.getExpressionList();
    for (ASTExpression e : eList) {
      e.accept(getTraverser());
      TypeCheckResult eValue = getTypeCheckResult().copy();
      if (!fValue.getResult().deepEquals(eValue.getResult())) {
        var start = node.get_SourcePositionStart();
        var end = node.get_SourcePositionEnd();
        Log.error("Stream Expressions cannot contain multiple types", start, end);
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      }
    }
  }

  @Override
  public void endVisit(ASTStreamConstructorExpression expr) {
    TypeCheckResult inner = getTypeCheckResult().copy();
    getTypeCheckResult().setResult(StreamSymTypeFactory.createStream(inner.getResult()));
  }
}
