/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._ast.ASTKerMLSpecialization;
import de.monticore.lang.kermlelements._ast.ASTPackageDeclaration;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsArtifactScope;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Command line interface for parsing KerML models and exporting their types. */
public class KerMLTool extends KerMLToolTOP {

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);

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

    OOSymbolsMill.init();
    IOOSymbolsArtifactScope exportScope = OOSymbolsMill.artifactScope();
    exportScope.setName("");
    exportScope.setPackageName(determinePackageName(
        (ASTKerMLModel) scope.getAstNode()));

    DatatypeExtractor datatypeExtractor = new DatatypeExtractor(exportScope);
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(datatypeExtractor);
    scope.getAstNode().accept(traverser);
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

  /** Extracts KerML datatypes and converts them to serializable type symbols. */
  protected static class DatatypeExtractor implements KerMLElementsVisitor2 {

    protected final IOOSymbolsArtifactScope exportScope;
    protected final Map<ASTDatatype, Datatype2TypeSymbolAdapter> exportedTypes =
        new LinkedHashMap<>();
    protected final Map<String, Datatype2TypeSymbolAdapter> exportedTypesByName =
        new LinkedHashMap<>();

    protected DatatypeExtractor(IOOSymbolsArtifactScope exportScope) {
      this.exportScope = exportScope;
    }

    @Override
    public void visit(ASTDatatype node) {
      if (node.isPresentSymbol()) {
        Datatype2TypeSymbolAdapter type =
            new Datatype2TypeSymbolAdapter(node.getSymbol());
        type.setPackageName(exportScope.getPackageName());
        exportScope.add(type);
        exportedTypes.put(node, type);
        exportedTypesByName.put(type.getName(), type);
      }
    }

    /** Connects specializations after all datatypes have been collected. */
    protected void completeSuperTypes() {
      exportedTypes.forEach((node, type) ->
          node.getKerMLRelationClauseList().stream()
              .filter(ASTKerMLSpecialization.class::isInstance)
              .map(ASTKerMLSpecialization.class::cast)
              .flatMap(specialization ->
                  specialization.getSpecializedList().stream())
              .map(superTypeName ->
                  exportedTypesByName.get(superTypeName.getBaseName()))
              .filter(java.util.Objects::nonNull)
              .map(SymTypeExpressionFactory::createTypeObject)
              .forEach(type::addSuperTypes));
    }
  }
}
