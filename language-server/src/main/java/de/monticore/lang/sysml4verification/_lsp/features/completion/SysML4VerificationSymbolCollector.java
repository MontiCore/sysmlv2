package de.monticore.lang.sysml4verification._lsp.features.completion;

import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationArtifactScope;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;

public class SysML4VerificationSymbolCollector extends SysML4VerificationSymbolCollectorTOP {

  @Override
  public void visit(ISysML4VerificationScope node) {
    node.getSubScopes().stream().forEach(s -> visit(s));
    super.visit(node);
  }

  @Override
  public void visit(ISysML4VerificationArtifactScope node) {
    node.getSubScopes().stream().forEach(s -> visit(s));
    super.visit(node);
  }
}
