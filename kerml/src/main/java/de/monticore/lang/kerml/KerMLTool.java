/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._symboltable.IKerMLArtifactScope;
import de.monticore.lang.kermlelements._ast.ASTKerMLSuperclassing;
import de.monticore.lang.kermlelements._ast.ASTKerMLTyping;
import de.monticore.lang.kermlelements._ast.ASTSpecialization;
import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.lang.kermlelements._symboltable.FeatureSymbol;
import de.monticore.lang.kermlelements._symboltable.IKerMLElementsScope;
import de.monticore.lang.kermlelements._symboltable.KerMLPackageSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.DefaultParser;
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
    var options = initOptions();
    try {
      var cmd = new DefaultParser().parse(options, args);
      if (cmd.hasOption("help")) {
        printHelp(options);
      } else if (cmd.hasOption("version")) {
        printVersion();
      } else if (!cmd.hasOption("input")) {
        Log.error("No KerML input file specified. Use -i <file>.");
      } else {
        var model = parse(cmd.getOptionValue("input"));
        if (model == null) {
          return;
        }
        var scope = createSymbolTable(model);
        if (cmd.hasOption("symboltable")) {
          storeSymbols(scope, cmd.getOptionValue("symboltable"));
        }
      }
    } catch (ParseException e) {
      Log.error("Could not process KerMLTool parameters: " + e.getMessage());
    }
  }

  @Override
  public void completeSymbolTable(ASTKerMLModel node) {
    var traverser = KerMLMill.traverser();
    traverser.add4KerMLElements(new KerMLTypesCompleter());
    node.accept(traverser);
  }

  /**
   * Erstellt aus dem ArtifactScope eine separate Symboltabelle für den Export
   * und schreibt sie als kermlsym Datei.
   */
  @Override
  public void storeSymbols(IKerMLArtifactScope scope, String path) {
    var exportScope = KerMLMill.artifactScope();
    if (scope.isPresentName()) {
      exportScope.setName(scope.getName());
    }
    exportScope.setPackageName(scope.getPackageName());
    exportDatatypes(scope, exportScope);
    super.storeSymbols(exportScope, path);
  }

  private void exportDatatypes(IKerMLElementsScope source, IKerMLElementsScope target) {
    for (var datatype : source.getLocalDatatypeSymbols()) {
      var parameters = BasicSymbolsMill.scope();
      // Sammelt die Namen in Fundreihenfolge und entfernt Duplikate.
      var names = typeVariableNames(datatype, new HashSet<>())
          .collect(Collectors.toCollection(LinkedHashSet::new));
      for (var name : names) {
        parameters.add(BasicSymbolsMill.typeVarSymbolBuilder().setName(name)
            .setSpannedScope(BasicSymbolsMill.scope()).build());
      }
      target.add(BasicSymbolsMill.typeSymbolBuilder().setName(datatype.getName())
          .setFullName(datatype.getFullName())
          .setPackageName(datatype.getPackageName())
          .setSpannedScope(parameters).build());
    }

    for (var subScope : source.getSubScopes()) {
      var targetScope = target;
      if (subScope.isPresentSpanningSymbol()
          && subScope.getSpanningSymbol() instanceof KerMLPackageSymbol) {
        var sourcePackage = (KerMLPackageSymbol) subScope.getSpanningSymbol();
        targetScope = KerMLMill.scope();
        target.add(KerMLMill.kerMLPackageSymbolBuilder().setName(sourcePackage.getName())
            .setFullName(sourcePackage.getFullName())
            .setPackageName(sourcePackage.getPackageName())
            .setSpannedScope(targetScope).build());
        target.addSubScope(targetScope);
      }
      exportDatatypes(subScope, targetScope);
    }
  }

  private Stream<String> typeVariableNames(DatatypeSymbol datatype, Set<DatatypeSymbol> visited) {
    if (!visited.add(datatype)) {
      return Stream.empty();
    }
    var features = new LinkedHashMap<String, FeatureSymbol>();
    collectEffectiveFeatures(datatype, features, new HashSet<>());
    return features.values().stream().flatMap(feature -> {
      var types = featureTypes(feature).collect(Collectors.toList());
      if (types.isEmpty() && feature.isPresentAstNode()) {
        return Stream.of(feature.getName());
      }
      return types.stream().flatMap(name -> "Anything".equals(name)
          ? Stream.of(feature.getName())
          : findDatatype(feature.getEnclosingScope(), name).stream()
              .flatMap(referenced -> typeVariableNames(referenced, visited)));
    });
  }

  private void collectEffectiveFeatures(DatatypeSymbol datatype,
      Map<String, FeatureSymbol> features, Set<DatatypeSymbol> visiting) {
    if (!visiting.add(datatype)) {
      return;
    }
    if (datatype.isPresentAstNode()) {
      specializationTypes(datatype.getAstNode().streamSpecializations(), ASTKerMLSuperclassing.class)
          .flatMap(name -> findDatatype(datatype.getEnclosingScope(), name).stream())
          .forEach(parent -> collectEffectiveFeatures(parent, features, visiting));
    }
    // Ein späteres Feature mit Typangabe ersetzt ein früheres mit demselben Namen.
    for (var feature : datatype.getSpannedScope().getLocalFeatureSymbols()) {
      features.merge(feature.getName(), feature,
          (inherited, declared) -> featureTypes(declared).findAny().isEmpty() ? inherited : declared);
    }
    visiting.remove(datatype);
  }

  private Stream<String> featureTypes(FeatureSymbol feature) {
    return feature.isEmptyTypes() && feature.isPresentAstNode()
        ? specializationTypes(feature.getAstNode().streamSpecializations(), ASTKerMLTyping.class)
        : feature.streamTypes().map(SymTypeExpression::printFullName);
  }

  private Stream<String> specializationTypes(Stream<ASTSpecialization> specializations,
      Class<? extends ASTSpecialization> kind) {
    return specializations.filter(kind::isInstance)
        .flatMap(ASTSpecialization::streamSuperTypes).map(ASTMCType::printType);
  }

  private Optional<DatatypeSymbol> findDatatype(IKerMLElementsScope scope, String name) {
    String qualifiedName = name.replace("::", ".");
    return Stream.iterate(scope, Objects::nonNull, IKerMLElementsScope::getEnclosingScope)
        .map(current -> current.resolveDatatypeDown(name))
        .flatMap(Optional::stream).findFirst();
  }
}
