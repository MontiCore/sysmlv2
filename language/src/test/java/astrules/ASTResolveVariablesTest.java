package astrules;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlbasis._symboltable.ISysMLBasisScope;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diese Testklasse validiert die Resolve-Funktion für Variablenreferenzen in SysMLv2-Code.
 *
 * testCreateSymFiles erstellt symboltable-Dateien, um damit die Resolve-Funktion
 * zu testen ohne dass sie Zugriff auf den AST benötigt.
 *
 * testResolveOfVariables testet die Auflösung von Variablen wie 'a' und 'a.b' in einem Beispielmodell.
 *
 */
public class ASTResolveVariablesTest {

  private static final String JSON_OUTPUT_DIR = "src/test/resources/symSymbolTables";
  private static final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }

  //@Test
  public void testCreateSymFiles() throws IOException {
    List<String> models = List.of(
        "attribute def A { attribute b: boolean; }",
        "attribute def A2 { attribute a:A; attribute b2: boolean; }"
    );

    // Model verarbeiten
    for (var model : models) {
      var ast = SysMLv2Mill.parser().parse_String(model).get();
      var artifactScope = tool.createSymbolTable(ast);
      tool.completeSymbolTable(ast);
      tool.finalizeSymbolTable(ast);
      tool.storeSymbols(artifactScope,
          JSON_OUTPUT_DIR + "/" + artifactScope.getName() + ".sym");
    }
  }

  // Der test soll fehlschlagen, da die resolve Funktion a.b aktuell
  // noch nicht finden kann
  //@Test
  public void testResolveOfVariable() throws IOException {
    String model = "attribute a:A; constraint { a.b }";
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    ISysMLBasisScope scope = ast.getSysMLElement(1).getEnclosingScope();
    Optional<VariableSymbol> a = scope.resolveVariable("a");
    assertThat(a).isPresent();
    Optional<VariableSymbol> ab = scope.resolveVariable("a.b");
    assertThat(ab).isPresent();
  }

  // Der test soll fehlschlagen, da die resolve Funktion a2.b2 und a2.a.b
  // aktuell noch nicht finden kann
  //@Test
  public void testResolveOfVariable2() throws IOException {
    String model = "attribute a2:A2; constraint { a2.a.b }";
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    ISysMLBasisScope scope = ast.getSysMLElement(1).getEnclosingScope();
    Optional<VariableSymbol> a2 = scope.resolveVariable("a2");
    assertThat(a2).isPresent();
    Optional<VariableSymbol> a2b2 = scope.resolveVariable("a2.b2");
    assertThat(a2b2).isPresent();
    Optional<VariableSymbol> a2ab = scope.resolveVariable("a2.a.b");
    assertThat(a2ab).isPresent();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
