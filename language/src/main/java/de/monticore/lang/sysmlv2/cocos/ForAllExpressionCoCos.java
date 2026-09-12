package de.monticore.lang.sysmlv2.cocos;

import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.ocl.oclexpressions._cocos.OCLExpressionsASTForallExpressionCoCo;
import de.se_rwth.commons.logging.Log;

public class ForAllExpressionCoCos implements
    OCLExpressionsASTForallExpressionCoCo {
  @Override
  public void check(ASTForallExpression node) {
    for (var decl : node.getInDeclarationList())
      if (decl.isPresentExpression()){
        Log.warn("0x10090 OCL-style forall expressions are not standard compliant, consider using ControlFunctions::forAll");
      }
      else {
        Log.warn("0x10091 OCL-style forall expressions are not standard compliant, consider using ControlFunctions::forAll");
      }
    }
  }

