package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.sysmlexpressions._ast.ASTExistsExpression;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;

public class SysMLOCLExpressionsTypeVisitor extends OCLExpressionsTypeVisitor implements
    SysMLExpressionsVisitor2 {
  @Override
  public void endVisit(ASTExistsExpression node) {
    endVisit((de.monticore.ocl.oclexpressions._ast.ASTExistsExpression) node);
  }
}
