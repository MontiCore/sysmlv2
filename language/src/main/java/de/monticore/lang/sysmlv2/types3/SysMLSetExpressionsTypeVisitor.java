package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.sysmlexpressions._ast.ASTElementOfExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.ocl.setexpressions._ast.ASTSetInExpressionBuilder;
import de.monticore.ocl.setexpressions.types3.SetExpressionsTypeVisitor;

public class SysMLSetExpressionsTypeVisitor extends SetExpressionsTypeVisitor implements
    SysMLExpressionsVisitor2 {
  @Override
  public void endVisit(ASTElementOfExpression node) {
    var setInExpr = new ASTSetInExpressionBuilder()
        .setElem(node.getLeft())
        .setSet(node.getRight())
        .set_SourcePositionStart(node.get_SourcePositionStart())
        .set_SourcePositionEnd(node.get_SourcePositionEnd())
        .build();

    endVisit(setInExpr);

    getType4Ast().setTypeOfExpression(node, getType4Ast().getTypeOfExpression(setInExpr));
  }
}
