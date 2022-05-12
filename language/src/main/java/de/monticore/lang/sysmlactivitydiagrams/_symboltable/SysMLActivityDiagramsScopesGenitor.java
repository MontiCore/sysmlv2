package de.monticore.lang.sysmlactivitydiagrams._symboltable;

import de.monticore.lang.sysmlactivitydiagrams._ast.ASTActionUsage;
import de.monticore.lang.sysmlactivitydiagrams._ast.ASTSendActionUsage;

public class SysMLActivityDiagramsScopesGenitor extends SysMLActivityDiagramsScopesGenitorTOP {
  private int counterActionUsage = 1;

  /** Determines the names of action usages. Generates a name if the AST has none. */
  @Override
  public void visit(ASTActionUsage node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrectly. See issue #23
    if(!node.isPresentName()) {
      if(node.getDeclaration() != null && node.getDeclaration().isPresentName()) {
        node.setName(node.getDeclaration().getName());
      }
      else {
        node.setName("Unnamed ActionUsage" + counterActionUsage);
        counterActionUsage++;
      }
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public  void visit (ASTSendActionUsage node)  {
    visit((ASTActionUsage) node);
  }
}
