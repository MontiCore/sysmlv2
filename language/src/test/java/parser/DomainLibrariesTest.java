package parser;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTCalcDefPowerExpression;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diese Klasse sammelt alle Tests zu den SysML Domain Libraries (abgelegt unter resources).
 * Ziel ist es die Grammatiken genau so weit aufzubohren, dass die Modelle parsen.
 */
public class DomainLibrariesTest {

  static final String domainLibraries = "src/main/resources/Domain Libraries";

  static SysMLv2Tool tool;

  @BeforeAll
  public static void setup() {
    tool = new SysMLv2Tool();
  }

  @BeforeEach
  public void init() {
    tool.init();
    Log.init();
  }

  @Test
  public void testIdentifiers() throws IOException {
    long startTime = System.nanoTime();
    tool = new SysMLv2Tool();
    long newToolTime = System.nanoTime() - startTime;
    startTime = System.nanoTime();
    tool.init();
    long initTime = System.nanoTime() - startTime;
    startTime = System.nanoTime();
    Log.init();
    long logTime = System.nanoTime() - startTime;
    var model = "attribute <g> gram;";
    startTime = System.nanoTime();
    var ast = SysMLv2Mill.parser().parse_String(model);
    long parseTime = System.nanoTime() - startTime;
    Log.info(""+newToolTime/1000000+"ms", "new Tool()");
    Log.info(""+initTime/1000000+"ms", "tool.init()");
    Log.info(""+logTime/1000000+"ms", "Log.init()");
    Log.info(""+parseTime/1000000+"ms", "parse");
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testENotation() throws IOException {
    var model = "1.0e-10";
    var ast = SysMLv2Mill.parser().parse_StringScientificENotatationLiteral(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testSpecialCharacters() throws IOException {
    var model = "attribute <S> siemens : ConductanceUnit = 'Ω'^-1;";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var attr = (ASTAttributeUsage)ast.get().getSysMLElement(0);
    var base = ((ASTCalcDefPowerExpression)attr.getDefaultValue().getExpression()).getBase();
    assertThat(base).isInstanceOf(ASTNameExpression.class);
    assertThat(((ASTNameExpression)base).getName()).isEqualTo("Ω");
  }

  @Test
  public void testName() throws IOException {
    var model = "attribute <S> siemens : ConductanceUnit = Ohm^-1;";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var attr = (ASTAttributeUsage)ast.get().getSysMLElement(0);
    var base = ((ASTCalcDefPowerExpression)attr.getDefaultValue().getExpression()).getBase();
    assertThat(base).isInstanceOf(ASTNameExpression.class);
    assertThat(((ASTNameExpression)base).getName()).isEqualTo("Ohm");
  }

  @Test
  public void testKardinalität() throws IOException {
    var model = "attribute temperatureDifference: TemperatureDifferenceValue [*] nonunique :> scalarQuantities;";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testOrAndExpression() throws IOException {
    var model = "assert constraint boundMatch { (isBound == mRef.isBound) or (not isBound and mRef.isBound) }";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testCardinalities() throws IOException {
    // Wenn n im Kontext existiert, sollte das eine valide Kardinalität sein
    var model = "[1..n]";
    var ast = SysMLv2Mill.parser().parse_StringSysMLCardinality(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testFunctionExpression() throws IOException {
    var model = ""
        + "(1..size(edges))->forAll {                                   \n"
        + "  in i;                                                      \n"
        + "  edges#(i).vertices->equals(                                \n"
        + "    (vertices#((2*i)-1), vertices#(2*i))                     \n"
        + "  )                                                          \n"
        + "  and                                                        \n"
        + "  includes(                                                  \n"
        + "    (edges#(i).vertices#(2) as Item).matingOccurrences,      \n"
        + "    edges#(if i==size(edges) ? 1 else i+1).vertices#(1)      \n"
        + "  )                                                          \n"
        + "}";
    var ast = SysMLv2Mill.parser().parse_StringExpression(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testFunctionExpression1() throws IOException {
    var model = ""
        + "  includes(                                                  \n"
        + "    (edges#(i).vertices#(2) as Item).matingOccurrences,      \n"
        + "    edges#(if i==size(edges) ? 1 else i+1).vertices#(1)      \n"
        + "  )";
    //model = "(edges#(i).vertices#(2) as Item).matingOccurrences";
    var ast = SysMLv2Mill.parser().parse_StringExpression(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParseShapeItems() {
    var ast = tool.parse(domainLibraries + "/Geometry/ShapeItems.sysml");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParse() {
    var ast = tool.parse(domainLibraries + "/Quantities and Units/SI.sysml");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParseISQ() {
    var ast = tool.parse(domainLibraries + "/Quantities and Units/ISQ.sysml");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParseQuantities() {
    var ast = tool.parse(domainLibraries + "/Quantities and Units/Quantities.sysml");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParseAll() throws IOException {
    var models = Files.walk(Path.of(domainLibraries))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(37);
    var successful = 0;
    var lines = 0;

    Log.enableFailQuick(false);
    for(var model: models) {
      try {
        var ast = tool.parse(model.toString());
        if(Log.getFindings().isEmpty()) {
          successful++;
        }
      } catch (Exception e) {
        // Erstmal nur messen
      }
      finally {
        lines += Log.getFindingsCount();
        Log.clearFindings();
      }
    }

    //System.out.println("Success rate: " + successful + "/" + 37 + " (" + lines + " findings)");
    assertThat(successful).isEqualTo(37);
    assertThat(Log.getFindings()).isEmpty();
  }

}
