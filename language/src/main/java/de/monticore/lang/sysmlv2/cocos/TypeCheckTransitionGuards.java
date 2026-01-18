package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

@Deprecated
public class TypeCheckTransitionGuards implements SysMLStatesASTSysMLTransitionCoCo {

  @Override
  public void check(ASTSysMLTransition node) {
    if(node.isPresentGuard()) {
      // Expression ausgraben
      ASTExpression expr = node.getGuard();

      // TypeCheck (Deriver) initialisieren
      var deriver = new SysMLDeriver(false);

      try {
        TypeCheckResult type = deriver.deriveType(expr);
        if(!type.isPresentResult() || type.getResult().getTypeInfo() == null) {
          Log.error("0x80004 Failed to derive a type!",
              expr.get_SourcePositionStart(),
              expr.get_SourcePositionEnd());
        }
        else if(!type.getResult().printFullName().equals("boolean")) {
          Log.error("0x80005 The expression type is '" + type.getResult().printFullName() + "' but should be boolean!",
              expr.get_SourcePositionStart(),
              expr.get_SourcePositionEnd());
        }
      }
      catch (Exception e) {
        Log.error("0x80006 " + e.getClass().getSimpleName() + " while type checking!",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
      }
    }
  }

}
