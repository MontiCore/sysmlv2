/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kerml._symboltable.KerMLSymbols2Json;
import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.lang.kermlelements._symboltable.IKerMLElementsScope;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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
  public IKerMLArtifactScope createSymbolTable(ASTKerMLModel ast) {
    var scope = super.createSymbolTable(ast);
    addTypeVariables(scope);
    return scope;
  }

  private void addTypeVariables(IKerMLElementsScope scope) {
    for (var datatype : scope.getLocalDatatypeSymbols()) {
      int typeVarCount = 0;
      for (var element : datatype.getAstNode().getKerMLElementList()) {
        if (element instanceof ASTFeature) {
          var feature = (ASTFeature) element;
          if (feature.isPresentType()) {
            String type = feature.getType().getQName();
            if (type.equals("Base.Anything")) {
              typeVarCount++;
            }
          }
        }
      }
      if (typeVarCount == 0) {
        switch (datatype.getFullName()) {
          case "Collections.Collection":
          case "Collections.OrderedCollection":
          case "Collections.UniqueCollection":
          case "Collections.Array":
          case "Collections.Bag":
          case "Collections.Set":
          case "Collections.OrderedSet":
          case "Collections.List":
            typeVarCount = 1;
            break;
          case "Collections.Map":
          case "Collections.OrderedMap":
            typeVarCount = 2;
            break;
        }
      }
      for (int i = 0; i < typeVarCount; i++) {
        datatype.getSpannedScope().add(BasicSymbolsMill.typeVarSymbolBuilder()
            .setName("T" + i)
            .setSpannedScope(KerMLMill.scope())
            .build());
      }
    }
    for (var subScope : scope.getSubScopes()) {
      addTypeVariables(subScope);
    }
  }

  @Override
  public void storeSymbols(IKerMLArtifactScope scope, String path) {
    KerMLMill.globalScope().putSymbolDeSer(
        DatatypeSymbol.class.getName(), new Datatype2TypeSymbolAdapter());
    new KerMLSymbols2Json().store(scope, path);
  }
}
