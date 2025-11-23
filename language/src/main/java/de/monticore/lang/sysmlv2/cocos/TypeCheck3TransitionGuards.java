package de.monticore.lang.sysmlv2.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class TypeCheck3TransitionGuards implements SysMLStatesASTSysMLTransitionCoCo {

  @Override
  public void check(ASTSysMLTransition node) {
    if(node.isPresentGuard()) {
      // Expression ausgraben
      ASTExpression expr = node.getGuard();

      try {
        SymTypeExpression type = TypeCheck3.typeOf(expr);
        if(!type.isObscureType()) {
          Log.error("0x80004 Failed to derive a type!",
              expr.get_SourcePositionStart(),
              expr.get_SourcePositionEnd());
        }
        else if(!type.isPrimitive() || !type.asPrimitive().getPrimitiveName().equals("boolean")) {
          Log.error("0x80005 The expression type is '" + type.printFullName() + "' but should be boolean!",
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
