package parser;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTElementOfExpression;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLInstantiation;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFunctionOperationExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionParserTest {

  private SysMLv2Parser parser;

  @BeforeAll
  public static void setup() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void clear() {
    parser = SysMLv2Mill.parser();
    Log.clearFindings();
  }

  /**
   * Checks that instantiation is parsed as such (and not as greater-than expressions or similar!)
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "b.append(new List<Packet>())",
      "b.append(new List<Packet>(para))",
      "b.append(new List<Packet>(para, para))"
  })
  public void testInstantiationInCallExpression(String expr) throws IOException {
    var ast = parser.parse_StringExpression(expr);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get()).isInstanceOf(ASTCallExpression.class);
    assertThat(((ASTCallExpression)ast.get()).getArguments().getExpressionList()).hasSize(1);
    assertThat(((ASTCallExpression)ast.get()).getArguments().getExpression(0))
        .isInstanceOf(ASTSysMLInstantiation.class);
  }

  /**
   * Checks that instantiation is parsed as such (and not as greater-than expressions or similar!)
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "new List<Packet>()",
      "new List<Packet>(para)",
      "new List<Packet>(para, para)"
  })
  public void testInstantiation(String expr) throws IOException {
    var ast = parser.parse_StringExpression(expr);

    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast).isPresent();
    assertThat(ast.get()).isInstanceOf(ASTSysMLInstantiation.class);
  }

  /**
   * Checks that "\in" is parsed as ElementOf
   */
  @Test
  public void testElementOf() throws IOException {
    var ast = parser.parse_StringExpression("a \\in mySet");

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get()).isInstanceOf(ASTElementOfExpression.class);
  }

  @Test
  public void testSysMLFunctionOperatorExprMinimal() throws IOException {
    var ast = parser.parse_StringExpression("x->b()");

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get())
        .isInstanceOf(ASTSysMLFunctionOperationExpression.class);
  }

  @Test
  public void testSysMLFunctionOperatorExpr() throws IOException {
    var ast = parser.parse_StringExpression("x->excludes(y)");

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get())
        .isInstanceOf(ASTSysMLFunctionOperationExpression.class);
  }

  @Test
  public void testSysMLFunctionOperatorExpr2() throws IOException {
    var ast = parser.parse_StringExpression("x->excludes(y.z)");

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get())
        .isInstanceOf(ASTSysMLFunctionOperationExpression.class);
  }

  /**
   * Checks that qualified names on both sides of a function operation
   * are parsed with the correct binding.
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "a.b->c.d",
      "a::b->c::d",
      "a::b->c.d",
      "a.b->c::d"
  })
  public void testQualifiedNamesInFunctionOperation(String expr)
      throws IOException {

    var ast = parser.parse_StringExpression(expr);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ast.get())
        .isInstanceOf(ASTSysMLFunctionOperationExpression.class);

    var functionOperation =
        (ASTSysMLFunctionOperationExpression) ast.get();

    assertThat(functionOperation.getExpression())
        .isInstanceOf(ASTFieldAccessExpression.class);
    assertThat(functionOperation.getSysMLQualifiedName().getPartsList())
        .containsExactly("c", "d");
  }

}
