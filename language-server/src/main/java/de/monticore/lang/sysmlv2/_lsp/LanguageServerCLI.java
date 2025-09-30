/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp;

import de.mclsg.CommandLineUtil;
import de.mclsg.LanguageServerOptions;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayoutBuilder;
import de.se_rwth.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LanguageServerCLI {

  public static final Logger logger = LoggerFactory.getLogger(SysMLv2LanguageServerCLI.class);

  public static void main(final String[] args) throws Exception {
    Log.init();
    LanguageServerOptions options = CommandLineUtil.parseOptions(args);

    SysMLv2LanguageServer server = new SysMLv2LanguageServerBuilder().layout(
        new ProjectLayoutBuilder().resources(options.getModelPaths()).build()
    ).build();
    SysMLv2LanguageServerCLI.start(server, options);
    server.exit();
  }

}
