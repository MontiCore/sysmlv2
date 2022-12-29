/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class SysMLv2ScopeManager extends SysMLv2ScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    super.initGlobalScope(modelPath);

    // Initialize Type Checker
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Override
  public boolean supportsIterativeScopeAppending() {
    return true;
  }

}
