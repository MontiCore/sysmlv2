/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayoutBuilder;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;

import java.io.File;

public class SysMLv2LanguageServer extends SysMLv2LanguageServerTOP {

  private static final Logger logger = LoggerFactory.getLogger(SysMLv2LanguageServer.class);

  /** Convenience: Wir modifizieren aktuell eigentlich nur den ModelPath */
  public SysMLv2LanguageServer(ProjectLayout layout) {
    this(
        new DocumentManager(),
        layout,
        new SysMLv2ScopeManager(),
        new SysMLv2SymbolUsageResolutionProvider()
    );
  }

  public SysMLv2LanguageServer(
      DocumentManager documentManager,
      ProjectLayout layout,
      SysMLv2ScopeManager scopeManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider)
  {
    super(documentManager, layout, scopeManager, symbolUsageResolutionProvider);

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
      resetContent(
          ProjectLayoutBuilder.from(layout).resources(sourceDir.toPath()).build()
      );
    });
  }
}
