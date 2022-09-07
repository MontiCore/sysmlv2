package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintBody;
import de.monticore.lang.sysmlparametrics._cocos.SysMLParametricsASTConstraintBodyCoCo;
import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.Objects;

/**
 * CoCo validates that the expression inside the constraint evaluates to a boolean value.
 */
public class ConstraintExpressionMustEvaluateToBoolean implements SysMLParametricsASTConstraintBodyCoCo {

  @Override
  public void check(ASTConstraintBody node) {
    if(node.isPresentExpression()) {
      ASTExpression expression = node.getExpression();
      TypeCheck typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());
      SymTypeExpression type = typeCheck.typeOf(expression);
      if(!Objects.equals(type.getTypeInfo().getName(), "boolean")) {
        Log.error("Constraint expression could not be evaluated to a boolean value.",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
    }
  }
}
