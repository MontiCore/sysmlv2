package de.monticore.lang.sysml4verification._lsp.features.symbols;

import de.mclsg.SeCommonsLogParser;
import de.mclsg.lsp.DocumentChangeType;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._lsp.SysML4VerificationDocumentInformationProvider;
import de.monticore.lang.sysml4verification._lsp.SysML4VerificationLanguageServer;
import de.monticore.lang.sysml4verification._lsp.SysML4VerificationSymbolUsageResolutionProvider;
import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationLanguageAccess;
import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationScopeManager;
import org.assertj.core.api.Fail;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.Optional;

/**
 * Base class for tests that require a document manager. Initializes the default DocumentManager and provides convenient
 * methods to add and create Documents.
 */
public abstract class TestWithDocumentManager {

  public DocumentManager documentManager;

  public void setUpDocumentManager() {
    this.documentManager = new DocumentManager();
  }

  public void prepareDocumentInformation(TextDocumentItem document) {
    SysML4VerificationDocumentInformationProvider documentInformationProvider
        = new SysML4VerificationDocumentInformationProvider(
            documentManager, new SeCommonsLogParser(), new SysML4VerificationLanguageAccess()
        );

    var modelPath = new ModelPath();

    var scopeMngr = new SysML4VerificationScopeManager();
    scopeMngr.initGlobalScope(modelPath);

    // TODO Lifecycle
    var server = new SysML4VerificationLanguageServer(
        documentManager,
        modelPath,
        scopeMngr,
        new SysML4VerificationSymbolUsageResolutionProvider()
    );
    var docInfoProv = server.getTextDocumentService().getDocumentInformationProvider();
    // TODO nur wegen: SysML4VerificationMill.init();

    docInfoProv.updateAllDocumentInformation(document, DocumentChangeType.CHANGED);
    Optional<DocumentInformation> documentInformation = documentManager.getDocumentInformation(document);
    if(documentInformation.isPresent()) {
      documentManager.updateDocumentInformation(documentInformation.get());
    }
    else {
      Fail.fail("documentInformation did not contain any value. This should not happen.");
    }
  }

  public TextDocumentItem addDocumentToDocumentManager(String uri, String content) {
    TextDocumentItem textDocumentItem = createDocumentItem(uri, content);
    this.documentManager.addDocument(textDocumentItem);
    return textDocumentItem;
  }

  private TextDocumentItem createDocumentItem(String uri, String content) {
    TextDocumentItem textDocumentItem = new TextDocumentItem();
    textDocumentItem.setUri(uri);
    textDocumentItem.setText(content);
    return textDocumentItem;
  }

}
