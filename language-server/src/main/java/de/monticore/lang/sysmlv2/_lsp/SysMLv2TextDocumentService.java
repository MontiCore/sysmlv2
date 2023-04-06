/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._lsp.features.symbols.SysMLv2DocumentSymbolProvider;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2LanguageAccess;
import org.eclipse.lsp4j.services.LanguageClient;

public class SysMLv2TextDocumentService extends SysMLv2TextDocumentServiceTOP {

  public SysMLv2TextDocumentService(CommonLanguageServer languageServer,
                                    DocumentManager documentManager,
                                    LanguageClient languageClient,
                                    SysMLv2LanguageAccess languageAccess,
                                    ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageClient, languageAccess, symbolUsageResolutionProvider);

    register(new SysMLv2DocumentSymbolProvider(documentManager));
  }





}

