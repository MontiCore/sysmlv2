/*package parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

  @Test
  public void testSonderfall() {
    LogStub.init();
    var tool = new KerMLTool();
    tool.init();

    // TODO Was war der Sonderfall, der zur Binding Strength "130" geführt hat:
    var ast = KerMLMill.getParser().parse_StringExpression("a#b").get();

    assertTrue(ast instanceof KerMLIndexExpression);
  }
} */
