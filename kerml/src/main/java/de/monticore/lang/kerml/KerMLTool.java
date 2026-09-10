/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kerml.symboltable.DatatypeExtractor;
import de.monticore.lang.kermlelements._ast.ASTPackageDeclaration;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsArtifactScope;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
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

  /**
   * Stores KerML datatypes as OO type symbols. OO type symbols are BasicSymbols
   * type symbols with support for the datatype specialization hierarchy.
   */
  @Override
  public void storeSymbols(IKerMLArtifactScope scope, String path) {
    if (!scope.isPresentAstNode()
        || !(scope.getAstNode() instanceof ASTKerMLModel)) {
      Log.error("Cannot export a KerML symbol table without its AST.");
      return;
    }

    ASTKerMLModel ast = (ASTKerMLModel) scope.getAstNode();
    OOSymbolsMill.init();
    IOOSymbolsArtifactScope exportScope = OOSymbolsMill.artifactScope();
    exportScope.setName("");
    exportScope.setPackageName(determinePackageName(ast));

    DatatypeExtractor datatypeExtractor = new DatatypeExtractor(exportScope);
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(datatypeExtractor);
    ast.accept(traverser);
    datatypeExtractor.completeSuperTypes();
    new OOSymbolsSymbols2Json().store(exportScope, path);
  }

  protected String determinePackageName(ASTKerMLModel ast) {
    return ast.getKerMLElementList().stream()
        .filter(ASTPackageDeclaration.class::isInstance)
        .map(ASTPackageDeclaration.class::cast)
        .map(ASTPackageDeclaration::getName)
        .findFirst()
        .orElse("");
  }
}
