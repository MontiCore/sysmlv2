package cocos;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConnectedPortsFitCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public class ConnectedPortsFitCoCoTest {

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
        "port def Booleans { in attribute val: Boolean; }",
        "port def MyBooleans { in attribute val: Boolean; }",
        "part def Inner { port i: Booleans; port o: ~Booleans; }",
        "part def MyInner { port i: MyBooleans; port o: ~MyBooleans; }"
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

  // Tests to show if Ports in a connection exists in the corresponding part
  @Test
  public void testValidParOutAndSubInPortExist() throws IOException {
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
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParentPortDoesNotExist() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + " part a: Inner;"
            + "connect o to a.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AC1");
  }

  @Test
  public void testSubPortDoesNotExist() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: Inner;"
            + "connect o to a.k; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AC2");
  }

  // In Arbeit
  @Test
  public void testInvalidParSubPortTypes() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: MyInner;"
            + "connect o to a.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AC4");
  }

  @Test
  public void testInvalidParPortTypes() throws IOException {
    String model =
        "part def Outer { port i: MyBooleans;"
            + "port o: ~Booleans; part a: MyInner;"
            + "connect i to o; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AC4");
  }

  @Test
  public void testInvalidSubPortTypes() throws IOException {
    String model =
        "part def Outer { port i: Booleans;"
            + "port o: ~Booleans; part a: MyInner; part b: Inner;"
            + "connect a.o to b.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    SysMLv2Mill.globalScope().setSymbolPath(new MCPath(Paths.get(JSON_OUTPUT_DIR)));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AC4");
  }
  @Disabled("Test zeigt dass CoCo verbinden von attributen wie Ports nicht unterstützt")
  @Test
  public void testBobHLR() throws IOException{
    String model = "part def Bob_HLR { " +
        " port input: boolean;" +
        " attribute input_attribute: boolean;" +
        " connect input to input_attribute;}";
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedPortsFitCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings()).isEmpty();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
