package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTCalcUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTCalcUsageCoCo;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that a calc usage contains at most one direct return usage.
 * Nested return usages are ignored.
 */
public class OneDirectReturnInCalcUsageCoCo implements SysMLActionsASTCalcUsageCoCo {
  @Override
  public void check(ASTCalcUsage node) {
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
      Log.error("0x10384 Calc usage must contain at most one direct return usage.", node.get_SourcePositionStart());
    }
  }
}
