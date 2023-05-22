package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;

public class SysMLv2LspCoCoRunner extends SysMLv2LspCoCoRunnerTOP {
  public SysMLv2LspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public boolean needsSymbols() {
    return true;
  }

}
