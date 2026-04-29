package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTCalcDefCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that a calc def contains at most one direct return.
 * Nested return usages are ignored.
 */
public class MaxOneDirectReturnInCalcDefCoCo implements SysMLActionsASTCalcDefCoCo {
  @Override
  public void check(ASTCalcDef node) {
    final int[] returnCount = {0};
    var traverser = SysMLv2Mill.inheritanceTraverser();

    traverser.add4SysMLBasis(new SysMLBasisVisitor2() {
      @Override
      public void visit(ASTAnonymousUsage retNode) {
        if (retNode.getModifier().isReturn() && retNode.getEnclosingScope() == node.getSpannedScope()) {
          returnCount[0]++;
        }
      }
    });

    traverser.add4SysMLParts(new SysMLPartsVisitor2() {
      @Override
      public void visit(ASTAttributeUsage retNode) {
        if (retNode.getModifier().isReturn() && retNode.getEnclosingScope() == node.getSpannedScope()) {
          returnCount[0]++;
        }
      }
    });

    node.accept(traverser);

    if (returnCount[0] > 1) {
      Log.error("0x10385 Calc def must contain at most one direct return.", node.get_SourcePositionStart());
    }
  }
}
