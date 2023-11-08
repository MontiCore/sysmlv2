import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLPower;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diese Klasse sammelt alle Tests zu den SysML Domain Libraries (abgelegt unter resources).
 * Ziel ist es die Grammatiken genau so weit aufzubohren, dass die Modelle parsen.
 */
public class DomainLibrariesTest {

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
    var expr = ((ASTSysMLPower)attr.getExpression()).getExpression(0);
    assertThat(expr).isInstanceOf(ASTNameExpression.class);
    assertThat(((ASTNameExpression)expr).getName()).isEqualTo("Ω");
  }

  @Test
  public void testName() throws IOException {
    var model = "attribute <S> siemens : ConductanceUnit = Ohm^-1;";
    var ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();

    var attr = (ASTAttributeUsage)ast.get().getSysMLElement(0);
    var expr = ((ASTSysMLPower)attr.getExpression()).getExpression(0);
    assertThat(expr).isInstanceOf(ASTNameExpression.class);
    assertThat(((ASTNameExpression)expr).getName()).isEqualTo("Ohm");
  }

  @Test
  public void testParse() {
    var ast = tool.parse("src/main/resources/SysML Domain Libraries/Quantities and Units/SI.sysml");

    assertThat(Log.getFindings()).isEmpty();
  }

}
