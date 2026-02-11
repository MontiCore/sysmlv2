package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._lsp.SysMLv2DocumentInformationFilter;
import de.se_rwth.commons.logging.Log;

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
    if(System.getenv("SYSML_DEFAULT_COCOS") == null) {
      tool.runDefaultCoCos(ast);
    }
    // Runs additional (verification-specific) CoCos when variable is set.
    // Defaults to not running them.
    if(System.getenv("SYSML_ADDITIONAL_COCOS") != null) {
      tool.runAdditionalCoCos(ast);
    }
  }

  @Override
  public void runCoCosForAllDocuments(){
    documentManager.getAllDocumentInformation(new SysMLv2DocumentInformationFilter()).forEach(di -> {
      Log.enableFailQuick(false);

      if(di.ast != null){
        Log.getFindings().clear();
        try {
          runAllCoCos((ASTSysMLModel) di.ast);
        } finally {
          di.findings.clear();
          di.findings.addAll(Log.getFindings());
          /*
           * did-change message from Client triggers runAllCoCos. Each Error
           * from those CoCos is logged in Findings and then added to
           * DocumentInformation. These are then displayed to the User.
           */
        }
      }
    });
  }


}
