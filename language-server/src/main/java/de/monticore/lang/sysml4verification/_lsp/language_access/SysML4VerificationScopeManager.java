package de.monticore.lang.sysml4verification._lsp.language_access;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class SysML4VerificationScopeManager extends SysML4VerificationScopeManagerTOP {

  @Override
  public void initGlobalScope(ModelPath modelPath) {
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
