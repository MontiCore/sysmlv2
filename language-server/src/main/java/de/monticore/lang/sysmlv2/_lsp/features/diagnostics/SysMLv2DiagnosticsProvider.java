/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.diagnostics;

import de.mclsg.SeCommonsLogParser;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2LanguageAccess;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;

public class SysMLv2DiagnosticsProvider extends SysMLv2DiagnosticsProviderTOP {

  private Boolean lock = false;

  public SysMLv2DiagnosticsProvider(
      DocumentManager documentManager,
      SeCommonsLogParser logParser,
      SysMLv2LanguageAccess languageAccess)
  {
    super(documentManager, logParser, languageAccess);
  }

  @Override
  public void handleTextDocument(TextDocumentItem documentItem, LanguageClient languageClient) {
    // Irgendwo im mclsg ist eine race condition. Das hier fixt sie. Dirty aber es funktioniert.
    synchronized (lock) {
      super.handleTextDocument(documentItem, languageClient);
    }
  }

}
