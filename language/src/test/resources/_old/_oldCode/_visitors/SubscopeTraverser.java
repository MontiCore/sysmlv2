package schrott._visitors;

import schrott._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverserImplementation;

/**
 * Diese Klasse stellt einen Traverser zur Verfügung, der alle Subscopes eines Scopes durchläuft.
 * Das Traversen über alle Subscopes ist notwendig, um den Namen des jeweiligen Subscopes zu setzen.
 * Wenn die Namen der Subscopes nicht korrekt gesetzt sind, schlägt das Resolven in Subscopes fehl. (see ScopeNameVisitor)
 * Dieser Code is Teil von Phase2+ (Filling Symbols and Scopes with Values) - siehe auch MC handbook Kapitel 9.6.2
 */
public class SubscopeTraverser extends SysML4VerificationTraverserImplementation {

  @Override
  public void traverse(ISysML4VerificationScope node) {
    if (getSysML4VerificationHandler().isPresent()) {
      getSysML4VerificationHandler().get().traverse(node);
    }
    else {
      // durchlaufe alle SubScopes eines Scopes
      for (ISysML4VerificationScope s : node.getSubScopes()) {
        s.accept(this);
      }
    }
  }
}
