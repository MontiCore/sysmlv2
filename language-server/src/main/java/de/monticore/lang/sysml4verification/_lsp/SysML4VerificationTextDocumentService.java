package de.monticore.lang.sysml4verification._lsp;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysml4verification.SysML4VerificationCodeActionProvider;
import de.monticore.lang.sysml4verification._lsp.features.symbols.SysML4VerificationDocumentSymbolProvider;
import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationLanguageAccess;
import org.eclipse.lsp4j.services.LanguageClient;

public class SysML4VerificationTextDocumentService extends SysML4VerificationTextDocumentServiceTOP {


  public SysML4VerificationTextDocumentService(
      DocumentManager documentManager,
      LanguageClient languageClient,
      SysML4VerificationLanguageAccess languageAccess,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider)
  {
    super(documentManager, languageClient, languageAccess, symbolUsageResolutionProvider);

    register(new SysML4VerificationCodeActionProvider(documentManager));
    register(new SysML4VerificationDocumentSymbolProvider(documentManager));
  }

}

