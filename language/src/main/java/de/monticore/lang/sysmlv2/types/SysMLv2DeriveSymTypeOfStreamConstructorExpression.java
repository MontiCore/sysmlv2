package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsHandler;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.types.check.AbstractDeriveFromExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

public class SysMLv2DeriveSymTypeOfStreamConstructorExpression extends AbstractDeriveFromExpression implements StreamExpressionsVisitor2, StreamExpressionsHandler{
  protected StreamExpressionsTraverser traverser;
  protected boolean isStream;

  public SysMLv2DeriveSymTypeOfStreamConstructorExpression(boolean isStream) {
    super();
    this.isStream = isStream;
  }

  public StreamExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(StreamExpressionsTraverser streamExpressionsTraverser) {
    this.traverser = streamExpressionsTraverser;
  }

  @Override
  public void endVisit(ASTStreamConstructorExpression node) {
    SymTypeExpression type = getTypeCheckResult().getResult();
    calculateCorrectType(type);
  }

  protected void calculateCorrectType(SymTypeExpression type) {
    var streamType = SysMLv2Mill.globalScope().resolveType("Stream");
    if(streamType.isEmpty()) {
      Log.error("0x81010 Stream not defined in global scope. Initialize it with 'SysMLv2Mill.addStreamType()'!");
    }
    type = SymTypeExpressionFactory.createGenerics(streamType.get(), type);
    getTypeCheckResult().setResult(type);
    }
  }
