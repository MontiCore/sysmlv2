package de.monticore.lang.sysmlstatemachinediagrams._symboltable;

import de.monticore.lang.sysmlstatemachinediagrams._ast.ASTStateUsage;

public class SysMLStateMachineDiagramsScopesGenitor extends SysMLStateMachineDiagramsScopesGenitorTOP {

  private int counterStateUsage = 1;

  @Override
  public void visit(ASTStateUsage node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed StateUsage" + counterStateUsage);
      counterStateUsage += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }
}
