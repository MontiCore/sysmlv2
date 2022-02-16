package de.monticore.lang.sysml4verification._lsp.features.diagnostics;

import de.mclsg.SeCommonsLogParser;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationLanguageAccess;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;

public class SysML4VerificationDiagnosticsProvider extends SysML4VerificationDiagnosticsProviderTOP {

  private Boolean lock = false;

  public SysML4VerificationDiagnosticsProvider(
      DocumentManager documentManager,
      SeCommonsLogParser logParser,
      SysML4VerificationLanguageAccess languageAccess)
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
