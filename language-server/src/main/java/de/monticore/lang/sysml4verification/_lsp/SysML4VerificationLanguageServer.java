package de.monticore.lang.sysml4verification._lsp;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.io.paths.ModelPath;

import java.io.File;

import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationScopeManager;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class SysML4VerificationLanguageServer extends SysML4VerificationLanguageServerTOP {

  private static final Logger logger = LoggerFactory.getLogger(SysML4VerificationLanguageServer.class);

  /** Convenience: Wir modifizieren aktuell eigentlich nur den ModelPath */
  public SysML4VerificationLanguageServer(ModelPath modelPath) {
    this(
        new DocumentManager(),
        modelPath,
        new SysML4VerificationScopeManager(),
        new SysML4VerificationSymbolUsageResolutionProvider()
    );
  }

  public SysML4VerificationLanguageServer(
      DocumentManager documentManager,
      ModelPath modelPath,
      SysML4VerificationScopeManager scopeManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider)
  {
    super(documentManager, modelPath, scopeManager, symbolUsageResolutionProvider);

    getWorkspaceService().setReconfiguration("source", jsonElement -> {
      if (!jsonElement.isJsonPrimitive() || !jsonElement.getAsJsonPrimitive().isString()) {
        logger.warn("Model source setting is not a string");
        if (languageClient != null) {
          languageClient.showMessage(new MessageParams(MessageType.Error, "Invalid Model Location setting!"));
        }
        return;
      }

      String source = jsonElement.getAsJsonPrimitive().getAsString();
      logger.info("Trying to change model source directory to {}", source);
      File sourceDir = new File(source);
      if (!sourceDir.exists() || !sourceDir.isDirectory()) {
        logger.warn("New model source setting is not a directory: {}", source);
        if (languageClient != null) {
          languageClient.showMessage(
              new MessageParams(MessageType.Error, "Model Location setting is not a directory!"));
        }
        return;
      }

      logger.info("Changing model source directory to {}", source);
      if (languageClient != null) {
        languageClient.showMessage(new MessageParams(MessageType.Info, "Resetting SysML Language Server"));
      }
      resetContent(new ModelPath(sourceDir.toPath()));
    });
  }
}
