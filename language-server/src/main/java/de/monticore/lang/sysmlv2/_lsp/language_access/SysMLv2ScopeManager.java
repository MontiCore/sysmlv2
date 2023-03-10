/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;

public class SysMLv2ScopeManager extends SysMLv2ScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    super.initGlobalScope(modelPath);

    // Initialize Type Checker
    new SysMLv2Tool().init();
  }

  @Override
  public boolean supportsIterativeScopeAppending() {
    return true;
  }

  @Override
  public SysMLv2ArtifactScopeWithFindings createArtifactScope(
      ASTSysMLModel ast,
      ISysMLv2ArtifactScope oldArtifactScope
  ) {
    var scope = super.createArtifactScope(ast, oldArtifactScope);

    syncAccessGlobalScope(gs -> {
      new SysMLv2Tool().completeSymbolTable(ast);
    });
    return scope;
  }
}
