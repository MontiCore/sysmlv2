package de.monticore.lang.sysmlv2.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionListResolver implements ExpressionsBasisVisitor2 {
  public List<ASTAttributeUsage> getAttributeList() {
    return attributeList;
  }

  List<ASTAttributeUsage> attributeList = new ArrayList<>();

  @Override
  public void visit(ASTNameExpression nameExpression) {
    var attribute = AttributeResolveUtils.resolveExprInBehaviour(nameExpression,
        (SysMLv2Scope) nameExpression.getEnclosingScope());
    if(attribute!=null) {
      attribute.ifPresent(astAttributeUsage -> attributeList.add(astAttributeUsage));
    }
  }

}
