package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2.cocos.ParentSubConnectionCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für die SymbolTable Serialisierung und Deserialisierung.
 * Serialisierungstests werden nicht ausgeführt, damit kein AST erstellt wird.
 * Die vorproduzierten JSON Dateien befinden sich in resources/jsonSymbolTables.
 *
 * Ziel: Testen, dass CoCos auch ohne direkten Zugriff auf das AST funktionieren,
 * wenn nur die SymbolTable (als JSON) zur Verfügung steht.
 */
public class SymbolTableCreationTest {

  private static final String JSON_OUTPUT_DIR = "src/test/resources/jsonSymboltables";
  private static final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }
  /**
   * Erstellt 3 SymbolTables aus einem Model und speichert sie als JSON
   */
  //@Test
  public void testCreateJsonSymbolTable1() throws IOException {
    String model = "port def Booleans { in attribute val: Boolean; }"
        + "part def A { port input: Booleans; "
        + "port output: ~Booleans; }"
        + "part def B { port input: Booleans;"
        + "port output: ~Booleans; part a: A;"
        + "connect output to output; }";

    // Model verarbeiten
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var artifactScope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    // Serialize to JSON
    SysMLv2Symbols2Json serializer = new SysMLv2Symbols2Json();
    String jsonSymbolTable = serializer.serialize(artifactScope);

    // Validiere, dass JSON Datei erzeugt wurde
    assertThat(jsonSymbolTable).isNotEmpty();
    assertThat(jsonSymbolTable).contains("symbols");

    // Speichere JSON für manuelle Inspektion
    saveJsonToFile(jsonSymbolTable, "outToOut_model.json");
  }

  //@Test
  public void testCreateJsonSymbolTable2() throws IOException {
    String model = "port def Booleans { in attribute val: Boolean; }"
        + "part def A { port input: Booleans; "
        + "port output: ~Booleans; }"
        + "part def B { port input: Booleans;"
        + "port output: ~Booleans; part a: A;"
        + "connect output to a.input; }";

    // Model verarbeiten
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var artifactScope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    // Serialize to JSON
    SysMLv2Symbols2Json serializer = new SysMLv2Symbols2Json();
    String jsonSymbolTable = serializer.serialize(artifactScope);

    // Validiere, dass JSON Datei erzeugt wurde
    assertThat(jsonSymbolTable).isNotEmpty();
    assertThat(jsonSymbolTable).contains("symbols");

    // Speichere JSON für manuelle Inspektion
    saveJsonToFile(jsonSymbolTable, "outToSubIn_model.json");
  }

  //@Test
  public void testCreateSymbolTable3() throws IOException {
    String model = "port def Booleans { in attribute val: Boolean; }"
        + "part def A { port input: Booleans; "
        + "port output: ~Booleans; }"
        + "part def B { port input: Booleans;"
        + "port output: ~Booleans; part a: A;"
        + "connect a.input to a.input; }";

    // Model verarbeiten
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var artifactScope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    // Serialize to JSON
    SysMLv2Symbols2Json serializer = new SysMLv2Symbols2Json();
    String jsonSymbolTable = serializer.serialize(artifactScope);

    // Validiere, dass JSON Datei erzeugt wurde
    assertThat(jsonSymbolTable).isNotEmpty();
    assertThat(jsonSymbolTable).contains("symbols");

    // Speichere JSON für manuelle Inspektion
    saveJsonToFile(jsonSymbolTable, "SubInToSubIn_model.json");
  }

    /**
   * Test: Simuliert Server-Situation wo ASTConnectionUsage existiert,
   * aber symbol.getAstNode() nicht aktuell soll dieser Test fehlschlagen,
   * daher wird er nicht ausgeführt.
   */
  //@Test
  public void testASTwithScopeFromJSON() throws IOException {
    String model = "port def Booleans { in attribute val: Boolean; }"
        + "part def A { port input: Booleans; "
        + "port output: ~Booleans; }"
        + "part def B { port input: Booleans;"
        + "port output: ~Booleans; part a: A;"
        + "connect output to output; }";

    ASTSysMLModel originalAst = parse(model);

    var serverScope = tool.loadSymbols("src/test/resources/jsonSymboltables/outToOut_model.json");

    createSt(originalAst);
    originalAst.setEnclosingScope(serverScope);

    var errors = check(originalAst);

    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("0x10AB0");
  }

  private List<Finding> check(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ParentSubConnectionCoCo());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    return Log.getFindings().stream().filter(Finding::isError).collect(
        Collectors.toList());
  }

  /**
   * Speichert JSON-Dateien zur manuellen Analyse
   */
  private void saveJsonToFile(String json, String filename) {
    try {
      Path outputDir = Paths.get(JSON_OUTPUT_DIR);
      Files.createDirectories(outputDir);

      Path filePath = outputDir.resolve(filename);
      Files.writeString(filePath, json);

      System.out.println("✓ JSON gespeichert: " + filePath.toAbsolutePath());
    } catch (IOException e) {
      System.err.println("⚠ Konnte JSON nicht speichern: " + e.getMessage());
    }
  }

  private ISysMLv2ArtifactScope createSt(ASTSysMLModel ast) {
    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    return scope;
  }

  private ASTSysMLModel parse(String model) throws IOException {
    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    return optAst.get();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }


}
