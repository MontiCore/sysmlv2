package de.monticore.lang.sysmlv2._lsp;

import de.mclsg.CommandLineUtil;
import de.mclsg.LanguageServerOptions;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Slf4jLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LanguageServerCLI {
  public static final Logger logger = LoggerFactory.getLogger(SysMLv2LanguageServerCLI.class);

  public static void main(final String[] args) throws Exception {
    Slf4jLog.init();
    LanguageServerOptions options = CommandLineUtil.parseOptions(args);
    MCPath modelPath = new MCPath(options.getModelPaths());

    SysMLv2LanguageServer server = new SysMLv2LanguageServer(modelPath);
    SysMLv2LanguageServerCLI.start(server, options);
    server.exit();
  }

}
