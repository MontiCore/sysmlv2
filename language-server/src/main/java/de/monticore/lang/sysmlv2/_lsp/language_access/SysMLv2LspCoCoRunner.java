package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;

public class SysMLv2LspCoCoRunner extends SysMLv2LspCoCoRunnerTOP {
  public SysMLv2LspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public boolean needsSymbols() {
    return true;
  }

  @Override
  public void runAllCoCos(ASTSysMLModel ast){
    tool.runDefaultCoCos(ast);
    // Runs additional (verification-specific) CoCos when variable is set.
    // Defaults to not running them.
    if(System.getenv("SYSML_ADDITIONAL_COCOS") != null) {
      tool.runAdditionalCoCos(ast);
    }
  }


}
