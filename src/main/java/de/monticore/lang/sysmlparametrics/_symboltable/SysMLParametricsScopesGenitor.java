package de.monticore.lang.sysmlparametrics._symboltable;

import de.monticore.lang.sysmlparametrics.SysMLParametricsMill;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintUsage;
import de.se_rwth.commons.logging.Log;

public class SysMLParametricsScopesGenitor extends SysMLParametricsScopesGenitorTOP {

  private int counterConstraintUsage = 1;

  @Override
  public void visit(ASTConstraintUsage node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConstraintUsage" + counterConstraintUsage);
      counterConstraintUsage += 1;
    }

    // The following part is copied from the generated SysMLParametricsScopesGenitorTOP file
    ConstraintUsageSymbol symbol = SysMLParametricsMill.constraintUsageSymbolBuilder().setName(
        node.getName()).build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    }
    else {
      Log.warn("0xA5021x27910 Symbol cannot be added to current scope, since no scope exists.");
    }
    ISysMLParametricsScope scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
    // symbol -> ast
    symbol.setAstNode(node);
    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());
    // scope -> ast
    scope.setAstNode(node);
    // ast -> scope
    node.setSpannedScope(scope);
    initConstraintUsageHP1(node.getSymbol());
  }
}
