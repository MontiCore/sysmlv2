/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;

public class SysMLv2LspCoCoRunner extends SysMLv2LspCoCoRunnerTOP {

  public SysMLv2LspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public void runAllCoCos(ASTSysMLModel ast) {
    new SysMLv2Tool().runDefaultCoCos(ast);
    new SysMLv2Tool().runAdditionalCoCos(ast);
  }

}
