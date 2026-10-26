/* (c) https://github.com/MontiCore/monticore */
package parser;

import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kermlelements._ast.ASTDocBlock;
import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlexpressions._ast.ASTKerMLBodyExpression;
import de.monticore.lang.kermlexpressions._ast
    .ASTKerMLFunctionOperationExpression;
import de.monticore.lang.kermlexpressions._ast.ASTNullCoalescingExpression;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KerMLFunctionOperationExpressionParserTest {

  @BeforeAll
  static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
    KerMLMill.reset();
    KerMLMill.init();
  }

  @Test
  void parsesQualifiedFunctionNames() throws IOException {
    var expression = parseOperation("a::b->c::d(1, 2)");

    assertEquals("c.d", expression.getMCQualifiedName().getQName());
    assertTrue(expression.isPresentArguments());
    assertEquals(2, expression.getArguments().getExpressionList().size());
    assertFalse(expression.isPresentBody());
    assertFalse(expression.isPresentFunctionReference());
  }

  @Test
  void parsesQualifiedFunctionReference() throws IOException {
    assertFunctionReference("factors -> reduce RealFunctions::'*'",
        "RealFunctions", "*");
  }

  @Test
  void parsesQuotedFunctionReference() throws IOException {
    assertFunctionReference("factors -> reduce '+'", "+");
  }

  @Test
  void parsesUnqualifiedFunctionReference() throws IOException {
    assertFunctionReference("factors -> reduce max", "max");
  }

  @Test
  void parsesOperationWithoutAdditionalArguments() throws IOException {
    var expression = parseOperation("values->count");

    assertEquals("count", expression.getMCQualifiedName().getQName());
    assertFalse(expression.isPresentArguments());
    assertFalse(expression.isPresentBody());
    assertFalse(expression.isPresentFunctionReference());
  }

  @Test
  void parsesArgumentsFollowedByBody() throws IOException {
    var expression = parseOperation("values->apply(1) {in x; x}");

    assertTrue(expression.isPresentArguments());
    assertTrue(expression.isPresentBody());
    assertFalse(expression.isPresentFunctionReference());
    assertEquals(1, expression.getArguments().getExpressionList().size());
    assertEquals(1, expression.getBody().getKerMLElementList().size());
  }

  @Test
  void parsesBodyWithDeclarations() throws IOException {
    var expression = parseOperation("values->reduce {"
        + "in x: Real; in y: Real; feature z = x * y; z}");

    assertTrue(expression.isPresentBody());
    assertEquals(3, expression.getBody().getKerMLElementList().size());
    assertFalse(expression.isPresentArguments());
    assertFalse(expression.isPresentFunctionReference());
  }

  @Test
  void parsesStandaloneBodyExpression() throws IOException {
    var parser = KerMLMill.parser();
    var ast = parser.parse_StringExpression("{in x; x + 1}");

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTKerMLBodyExpression);
    var body = (ASTKerMLBodyExpression) ast.get();
    assertEquals(1, body.getKerMLElementList().size());
    assertTrue(body.getInner() instanceof ASTPlusExpression);
  }

  @Test
  void preservesChaining() throws IOException {
    var expression = parseOperation("values->filter(1)->count()");

    assertEquals("count", expression.getMCQualifiedName().getQName());
    assertTrue(expression.getExpression()
        instanceof ASTKerMLFunctionOperationExpression);
    var previous = (ASTKerMLFunctionOperationExpression)
        expression.getExpression();
    assertEquals("filter", previous.getMCQualifiedName().getQName());
  }

  @Test
  void keepsNullCoalescingOutsideFunctionReference() throws IOException {
    var parser = KerMLMill.parser();
    var ast = parser.parse_StringExpression("values->reduce '+' ?? 0");

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTNullCoalescingExpression);
    var coalescing = (ASTNullCoalescingExpression) ast.get();
    assertTrue(coalescing.getLeft()
        instanceof ASTKerMLFunctionOperationExpression);
  }

  @Test
  void keepsFeatureBodyOutsideOperation() throws IOException {
    var parser = KerMLMill.parser();
    var ast = parser.parse_String(
        "feature result = values->reduce max { doc }");

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().getKerMLElement(0) instanceof ASTFeature);
    var feature = (ASTFeature) ast.get().getKerMLElement(0);
    assertTrue(feature.getExpression()
        instanceof ASTKerMLFunctionOperationExpression);
    assertEquals(1, feature.getKerMLElementList().size());
    assertTrue(feature.getKerMLElement(0) instanceof ASTDocBlock);
  }

  @Test
  void rejectsNumericFunctionReference() throws IOException {
    var parser = KerMLMill.parser();
    var ast = parser.parse_StringExpression("values->reduce 42");

    assertTrue(parser.hasErrors());
    assertTrue(ast.isEmpty());
  }

  private void assertFunctionReference(String input, String... parts)
      throws IOException {
    var expression = parseOperation(input);

    assertEquals("reduce", expression.getMCQualifiedName().getQName());
    assertTrue(expression.isPresentFunctionReference());
    assertEquals(List.of(parts),
        expression.getFunctionReference().getPartsList());
    assertFalse(expression.isPresentArguments());
    assertFalse(expression.isPresentBody());
  }

  private ASTKerMLFunctionOperationExpression parseOperation(String input)
      throws IOException {
    var parser = KerMLMill.parser();
    var ast = parser.parse_StringExpression(input);

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTKerMLFunctionOperationExpression,
        () -> input + ": " + ast.get().getClass().getSimpleName());
    return (ASTKerMLFunctionOperationExpression) ast.get();
  }
}
