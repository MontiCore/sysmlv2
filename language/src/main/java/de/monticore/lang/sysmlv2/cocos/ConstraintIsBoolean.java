/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.SourcePositionBuilder;
import de.se_rwth.commons.logging.Log;

public class ConstraintIsBoolean implements SysMLConstraintsASTConstraintUsageCoCo {

  @Override public void check(ASTConstraintUsage node) {
    // Expression ausgraben
    ASTExpression expr = node.getExpression();

    // TypeCheck (Deriver) initialisieren
    var deriver = new SysMLDeriver(true);

    try {
      TypeCheckResult type = deriver.deriveType(expr);
      if(!type.isPresentResult() || type.getResult().getTypeInfo() == null) {
        var start = node.get_SourcePositionStart();
        var end = constraintEnd(start);
        Log.error("0x80001 Failed to derive a type!", start, end);
      }
      else if(!type.getResult().printFullName().equals("boolean")) {
        Log.error("0x80002 The expression type is '" + type.getResult().printFullName() + "' but should be boolean!", expr.get_SourcePositionStart(), expr.get_SourcePositionEnd());
      }
    }
    catch (Exception e) {
      var start = node.get_SourcePositionStart();
      var end = constraintEnd(start);
      Log.error("0x80003 " + e.getClass().getSimpleName() + " while type checking!", start, end);
    }
  }

  private SourcePosition constraintEnd(SourcePosition start) {
    return new SourcePositionBuilder().setFileName(start.getFileName().get()).setLine(start.getLine()).setColumn(
        start.getColumn() + 11).build();
  }
}
