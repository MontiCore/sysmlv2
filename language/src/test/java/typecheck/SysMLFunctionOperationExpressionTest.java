package typecheck;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SysMLFunctionOperationExpressionTest {
  private SysMLv2Tool tool;

  @BeforeEach
  void setUp() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {"1->id", "1->id()", "1->id()->id()", "id(1->id())"})
  void testChainedFunctionOperation(String expression) throws IOException {
    assertType("calc def id { in value: int; return : int; }", expression, "int");
  }

  @Test
  void testValidArgs() throws IOException {
    assertType("calc def check { in value: int; in flag: boolean; return : boolean; }",
      "1->check(true)", "boolean");
  }

  @Disabled("CalcDefs können noch nicht überladen werden (0xA4095)")
  @ParameterizedTest
  @CsvSource({"1->choose(), boolean", "true->choose(), int"})
  void testOverloadedCalcDefs(String expression, String expectedType) throws IOException {
    assertType("calc def choose { in value: int; return : boolean; }"
        + "calc def choose { in value: boolean; return : int; }",
      expression, expectedType);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1->Functions::id()", "1->Functions.id()"})
  void resolvesQualifiedFunctionNames(String expression) throws IOException {
    assertType("package Functions { calc def id { in value: int; return : int; } }",
      expression, "int");
  }

  @ParameterizedTest
  @ValueSource(strings = {"true->check(true)", "1->check(2)", "1->check", "1->check(true, false)"})
  void testWrongArguments(String expression) throws IOException {
    assertInvalid("calc def check { in value: int; in flag: boolean; return : boolean; }",
      expression);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1->missing()", "1->number()"})
  void testWrongFunctionOperation(String expression) throws IOException {
    assertInvalid("attribute number: int;", expression);
    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().contains("0xC1234"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "values->ControlFunctions::forAll positive",
    "values->ControlFunctions::forAll(positive)"
  })
  void testLibraryFunction(String expression) throws IOException {
    assertType("attribute values: int[*];"
      + "calc def positive { in value: int; return : ScalarValues::Boolean; }",
      expression, "ScalarValues.Boolean");
  }

  @Test
  void testQualifiedFunctionReference() throws IOException {
    assertType("attribute values: int[*];"
      + "package Predicates {"
      + "  calc def positive { in value: int; return : ScalarValues::Boolean; }"
      + "}",
  "values->ControlFunctions::forAll Predicates::positive", "ScalarValues.Boolean");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "values->ControlFunctions::forAll missing",
    "values->ControlFunctions::forAll positive"
  })
  void testWrongFunctionReference(String expression) throws IOException {
    assertInvalid("attribute values: int[*];"
      + "calc def positive { in value: boolean; return : ScalarValues::Boolean; }",
      expression);
  }

  @Test
  void testBodyParameterShadowing() throws IOException {
    var ast = completeModel("calc def usesForAll {"
      + "in values: int[*]; return : ScalarValues::Boolean;"
      + "values->ControlFunctions::forAll { in values: int; values != 0 } }");
    var calculation = (ASTCalcDef) ast.getSysMLElement(0);
    assertThat(TypeCheck3.typeOf(calculation.getExpression()).printFullName())
      .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void testBodyParameterNonExporting() throws IOException {
    assertInvalid("attribute values: int[*];",
      "(values->ControlFunctions::forAll { in value: int; value != 0 }) & (value != 0)");
    assertThat(Log.getFindings()).anyMatch(f ->
      f.getMsg().contains("0xFD118") && f.getMsg().contains("\"value\""));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "values->ControlFunctions::forAll { in value: int[*]; true }",
    "values->ControlFunctions::forAll { in value: boolean; value }",
    "values->ControlFunctions::forAll { in value: int; value }"
  })
  void testWrongBody(String expression) throws IOException {
    assertInvalid("attribute values: int[*];", expression);
  }

  @Test
  void testUntypedBodyParameter() throws IOException {
    assertInvalid("attribute values: int[*];",
      "values->ControlFunctions::forAll { in value; value != 0 }");
    assertThat(Log.getFindings()).hasSize(1);
    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().contains("0xC1AC0"));
  }

  private void assertType(String declarations, String expression, String expectedType)
      throws IOException {
    SymTypeExpression type = typeOf(declarations, expression);
    assertThat(Log.getFindings()).isEmpty();
    assertThat(type.printFullName()).isEqualTo(expectedType);
  }

  private void assertInvalid(String declarations, String expression) throws IOException {
    SymTypeExpression type = typeOf(declarations, expression);
    assertThat(type.isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
  }

  private SymTypeExpression typeOf(String declarations, String expression) throws IOException {
    var ast = completeModel(declarations + "constraint { " + expression + " }");
    var constraint = (ASTConstraintUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    return TypeCheck3.typeOf(constraint.getExpression());
  }

  private ASTSysMLModel completeModel(String model) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    return ast;
  }
}
