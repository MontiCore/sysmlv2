package de.monticore.lang.sysml4verification._lsp;

import de.mclsg.CommandLineUtil;
import de.mclsg.LanguageServerOptions;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml4verification._lsp.language_access.SysML4VerificationScopeManager;
import de.se_rwth.commons.logging.Slf4jLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LanguageServerCLI {
  public static final Logger logger = LoggerFactory.getLogger(SysML4VerificationLanguageServerCLI.class);

  public static void main(final String[] args) throws Exception {
    Slf4jLog.init();
    LanguageServerOptions options = CommandLineUtil.parseOptions(args);
    ModelPath modelPath = new ModelPath(options.getModelPaths());

    SysML4VerificationLanguageServer server = new SysML4VerificationLanguageServer(modelPath);
    SysML4VerificationLanguageServerCLI.start(server, options);
    server.exit();
  }

}
