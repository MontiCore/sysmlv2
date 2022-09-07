package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLExpressionsDeriver;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class ConstraintIsBoolean implements SysMLConstraintsASTConstraintUsageCoCo {

  @Override public void check(ASTConstraintUsage node) {
    // Expression ausgraben
    ASTExpression expr = node.getExpression();

    // TypeCheck (Deriver) initialisieren
    var deriver = new SysMLExpressionsDeriver();

    // Abfahrt
    TypeCheckResult type = deriver.deriveType(expr);
    if(type.isPresentResult() && type.getResult().getTypeInfo() != null) {
      if(!type.getResult().printFullName().equals("boolean")) {
        Log.error("Should be boolean", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
    else {
      Log.error("Could not derive a type", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
