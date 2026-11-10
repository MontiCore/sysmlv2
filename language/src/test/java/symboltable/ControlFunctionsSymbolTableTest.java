package symboltable;

import de.monticore.lang.sysmlactions._ast.ASTCalcDef;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFunctionOperationExpression;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.check.SymTypeExpressionFactory;
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

public class ControlFunctionsSymbolTableTest {

  private SysMLv2Tool tool;

  @BeforeEach
  public void setUp() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @Test
  public void testForAllSignature() {
    var forAll = SysMLv2Mill.globalScope()
      .resolveFunction("ControlFunctions.forAll").orElseThrow();

    assertThat(forAll.getFullName()).isEqualTo("ControlFunctions.forAll");
    assertThat(forAll.getType().printFullName())
      .isEqualTo("ScalarValues.Boolean");
    assertThat(forAll.getTypeVariableList()).hasSize(1);
    assertThat(forAll.getParameterList())
      .extracting(parameter -> parameter.getName())
      .containsExactly("collection", "test");

    var elementType = SymTypeExpressionFactory.createTypeVariable(
      forAll.getTypeVariableList().get(0));
    var collection = forAll.getParameterList().get(0).getType();
    assertThat(collection.isGenericType()).isTrue();
    assertThat(collection.asGenericType().getTypeConstructorFullName())
      .isEqualTo("Collections.OrderedCollection");
    assertThat(collection.asGenericType().getArgumentList()).hasSize(1);
    assertThat(collection.asGenericType().getArgument(0).deepEquals(elementType))
      .isTrue();

    var predicate = forAll.getParameterList().get(1).getType();
    assertThat(predicate.isFunctionType()).isTrue();
    assertThat(predicate.asFunctionType().sizeArgumentTypes()).isEqualTo(1);
    assertThat(predicate.asFunctionType().getArgumentType(0).deepEquals(elementType)).isTrue();
    assertThat(predicate.asFunctionType().getType().deepEquals(forAll.getType())).isTrue();
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
      "'import ControlFunctions::forAll;', forAll, int, calc def",
      "'import ControlFunctions::*;', forAll, int, calc def",
      "'', ControlFunctions::forAll, int, calc def",
      "'', ControlFunctions::forAll, ScalarValues::Integer, calc",
      "'', ControlFunctions::forAll, ScalarValues::Boolean, calc"
  })
  public void testForAllCall(String imports, String functionName,
                            String elementType, String predicateKind) throws IOException {
    var model = imports
        + "attribute values: " + elementType + "[*];"
        + predicateKind + " test { in attribute argument: " + elementType
        + "; return : ScalarValues::Boolean; }"
        + "constraint { " + functionName + "(values, test) }";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var constraint = (ASTConstraintUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    var type = TypeCheck3.typeOf(constraint.getExpression());
    assertThat(type.printFullName()).as("Findings: %s", Log.getFindings())
        .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"int", "ScalarValues::Boolean"})
  public void testForAllWithCollectionArray(String elementType) throws IOException {
    var model = "import ControlFunctions::forAll;"
        + "attribute values: Collections::Array<" + elementType + ">;"
        + "calc def test { in argument: " + elementType + "; return : ScalarValues::Boolean; }"
        + "constraint { forAll(values, test) }";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var constraint = (ASTConstraintUsage) ast.getSysMLElement(3);
    var type = TypeCheck3.typeOf(constraint.getExpression());
    assertThat(type.printFullName()).as("Findings: %s", Log.getFindings())
        .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  @Disabled("Type checking von -> nicht implementiert")
  public void testForAllWithInlinePredicate() throws IOException {
    var model = "import ScalarValues::Boolean;\n"
        + "import ControlFunctions::forAll;\n"
        + "calc def usesForAll {\n"
        + "  in inputArray: int[*];\n"
        + "  return : Boolean;\n"
        + "\n"
        + "  inputArray->forAll {\n"
        + "    in input: int;\n"
        + "    input != 0\n"
        + "  }\n"
        + "}";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var calculation = (ASTCalcDef) ast.getSysMLElement(2);
    assertThat(calculation.getExpression())
      .isInstanceOf(ASTSysMLFunctionOperationExpression.class);
    var type = TypeCheck3.typeOf(calculation.getExpression());
    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean");
    assertThat(calculation.getSymbol().getReturnType().printFullName())
        .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @Disabled("Type checking von -> nicht implementiert")
  @ValueSource(strings = {
      "values->forAll(test)",
      "values->ControlFunctions::forAll(test)",
      "values->forAll test"
  })
  public void testForAllFunctionOperation(String invocation) throws IOException {
    var model = "import ControlFunctions::forAll;"
        + "attribute values: int[*];"
        + "calc def test { in argument: int; return : boolean; }"
        + "constraint { " + invocation + " }";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var constraint = (ASTConstraintUsage) ast.getSysMLElement(3);
    var type = TypeCheck3.typeOf(constraint.getExpression());
    assertThat(type.printFullName()).as("Findings: %s", Log.getFindings())
        .isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @Disabled("Type checking von -> nicht implementiert")
  @CsvSource({
      "int, input + 1",
      "ScalarValues::Boolean, input"
  })
  public void testRejectsInvalidInlinePredicate(String parameterType, String expression)
      throws IOException {
    var model = "import ControlFunctions::forAll;"
        + "calc def invalidForAll {"
        + "  in values: int[*];"
        + "  return : ScalarValues::Boolean;"
        + "  values->forAll { in input: " + parameterType + "; " + expression + " }"
        + "}";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var calculation = (ASTCalcDef) ast.getSysMLElement(1);
    assertThat(TypeCheck3.typeOf(calculation.getExpression()).isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
    assertThat(Log.getFindings()).allSatisfy(
        finding -> assertThat(finding.getMsg()).doesNotContain("internal error"));
  }

  @ParameterizedTest
  @CsvSource({
      "int, int",
      "ScalarValues::Boolean, ScalarValues::Boolean"
  })
  public void testRejectsIncompatiblePredicate(String parameterType, String returnType) throws IOException {
    var model = "import ControlFunctions::forAll;"
        + "attribute values: int[*];"
        + "calc def test { in attribute argument: " + parameterType + "; return : " + returnType + "; }"
        + "constraint { forAll(values, test) }";
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var constraint = (ASTConstraintUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    assertThat(TypeCheck3.typeOf(constraint.getExpression()).isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
    assertThat(Log.getFindings()).allSatisfy(
        finding -> assertThat(finding.getMsg()).doesNotContain("internal error"));
  }

  @Test
  public void testRepeatedRegistrationDoesNotDuplicateForAll() {
    var globalScope = SysMLv2Mill.globalScope();
    var forAll = globalScope.resolveFunction("ControlFunctions.forAll").orElseThrow();

    SysMLv2Mill.addControlFunctionsTypes();
    SysMLv2Mill.addControlFunctionsTypes();

    assertThat(globalScope.resolveFunctionMany("ControlFunctions.forAll")).containsExactly(forAll);
    assertThat(Log.getFindings()).isEmpty();
  }
}
