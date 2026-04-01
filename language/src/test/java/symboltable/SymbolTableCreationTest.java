package symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ParentSubConnectionCoCo;
import de.monticore.lang.sysmlv2.cocos.PortDefinitionExistsCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für die SymbolTable Serialisierung und Deserialisierung.
 * Serialisierungstests werden nicht ausgeführt, damit kein AST erstellt wird.
 * Die vorproduzierten .sym Dateien befinden sich in resources/jsonSymbolTables.
 * Ziel: Testen, dass CoCos auch ohne direkten Zugriff auf das AST funktionieren,
 * wenn nur die SymbolTable zur Verfügung steht.
 */
public class SymbolTableCreationTest {

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
  /**
   * Erstellt 3 SymbolTables aus einem Model und speichert sie als JSON
   */
  //@Test
  public void testCreateSymFiles() throws IOException {
    List<String> models = List.of(
        "port def Booleans { in attribute val: Boolean; }",
        "part def Inner { port i: Booleans; port o: ~Booleans; }"
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

    /**
   * Test: Simuliert Server-Situation wo Symboltable von Inner existiert,
   * der AST aber nicht.
   */
  @Test
  public void testParInToSubIn() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
        + "port o: ~Booleans; part a: Inner;"
        + "connect i to a.i; connect a.o to o; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ParentSubConnectionCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings()).isEmpty();  //Correkt example
    System.out.println(Log.getFindings());
  }

  /**
   * Die folgenden 3 Tests sollen jeweils überprüfen, ob die zugehörige CoCo den
   * Fall korrekt abfängt.
   */
  @Test
  public void testParOutToParOut() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: Inner;"
            + "connect o to o; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ParentSubConnectionCoCo());
    checker.checkAll(ast);          // ParentSubConnectionCoCo should trigger
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AB0");
    System.out.println(Log.getFindings());
  }

  @Test
  public void testParOutToSubIn() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: Inner;"
            + "connect o to a.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new PortDefinitionExistsCoCo());
    checker.addCoCo(new ParentSubConnectionCoCo());
    checker.checkAll(ast);      // ParentSubConnectionCoCo should trigger
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AB1");
    System.out.println(Log.getFindings());
  }


  @Test
  public void testSubInToSubIn() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: Inner;"
            + "connect a.i to a.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);    //löst 3 Warnings aus wenn symbol path zu spät gesetzt wird
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ParentSubConnectionCoCo());
    checker.checkAll(ast);      // ParentSubConnectionCoCo should trigger
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AB2");
    System.out.println(Log.getFindings());
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
