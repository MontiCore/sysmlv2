/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kerml._symboltable.KerMLSymbols2Json;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/** Command line interface for parsing KerML models and exporting their types. */
public class KerMLTool extends KerMLToolTOP {

  @Override
  public void init() {
    super.init();
    KerMLTypeCheck3.init();
  }

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
  public void completeSymbolTable(ASTKerMLModel node) {
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(new KerMLTypesCompleter());

    // Visiting artifact scope _and_ the AST requires two calls
    if (node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }
    node.accept(traverser);
  }

  @Override
  public void storeSymbols(IKerMLArtifactScope scope, String path) {
    var visitor = new DatatypeCollector();
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(visitor);
    scope.accept(traverser);

    var exportScope = KerMLMill.artifactScope();
    if (scope.isPresentName()) {
      exportScope.setName(scope.getName());
    }
    exportScope.setPackageName(scope.getPackageName());
    for (var datatype : visitor.getDatatypes()) {
      var type = new Datatype2TypeSymbolAdapter(datatype);
      type.setFullName(datatype.getFullName());
      type.setPackageName(datatype.getPackageName());
      exportScope.add(type);
    }
    new KerMLSymbols2Json().store(exportScope, path);
  }
}
