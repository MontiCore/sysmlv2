package de.monticore.lang.sysmlparametrics._symboltable;

import de.monticore.lang.sysmlparametrics.SysMLParametricsMill;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintUsage;
import de.se_rwth.commons.logging.Log;

public class SysMLParametricsScopesGenitor extends SysMLParametricsScopesGenitorTOP {

  private int counterConstraintUsage = 1;

  @Override
  public void visit(ASTConstraintUsage node) {
    // Name Symbol if it is unnamed. Otherwise, scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConstraintUsage" + counterConstraintUsage);
      counterConstraintUsage += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }
}
