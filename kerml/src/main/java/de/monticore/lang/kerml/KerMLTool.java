/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kerml.symboltable.DatatypeExtractor;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/** Command line interface for parsing KerML models and exporting their types. */
public class KerMLTool extends KerMLToolTOP {

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();

    try {
      CommandLine cmd = new DefaultParser().parse(options, args);

      if (cmd.hasOption("help")) {
        printHelp(options);
        return;
      }
      if (cmd.hasOption("version")) {
        printVersion();
        return;
      }
      if (!cmd.hasOption("input")) {
        Log.error("No KerML input file specified. Use -i <file>.");
        return;
      }

      ASTKerMLModel ast = parse(cmd.getOptionValue("input"));
      if (ast == null) {
        return;
      }

      IKerMLArtifactScope artifactScope = createSymbolTable(ast);

      if (cmd.hasOption("symboltable")) {
        storeSymbols(artifactScope, cmd.getOptionValue("symboltable"));
      }
    }
    catch (ParseException e) {
      Log.error("Could not process KerMLTool parameters: "
          + e.getMessage());
    }
  }

  @Override
  public void storeSymbols(IKerMLArtifactScope scope, String path) {
    BasicSymbolsMill.init();
    var exportScope = BasicSymbolsMill.artifactScope();
    var datatypeExtractor = new DatatypeExtractor(exportScope);
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(datatypeExtractor);
    ((ASTKerMLModel) scope.getAstNode()).accept(traverser);
    new BasicSymbolsSymbols2Json().store(exportScope, path);
  }
}
