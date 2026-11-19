package typecheck;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SysMLBodyExpressionInferenceTest {
  private static final String KNOWN_BODY =
    "constraint { { in value: ScalarValues.Boolean; value } }";

  private static final String BOOLEAN_CALC_DEF =
    "calc def isValid { in input: ScalarValues.Boolean; return : ScalarValues.Boolean; }";

  private static final String BODY_WITH_CALC_DEF =
    "constraint { { in value: ScalarValues.Boolean; isValid(value) } }";

  private static final String BODY_WITH_TARGET =
    "attribute values: ScalarValues.Boolean[*];"
    + "constraint {"
    + "  ControlFunctions::forAll(values, {"
    + "    in value: ScalarValues.Boolean;"
    + "    isValid(value)"
    + "  })"
    + "}";

  private SysMLv2Tool tool;

  @BeforeEach
  void setUp() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @Test
  void derivesTheFunctionTypeOfABodyWithAKnownResult() throws IOException {
    var type = TypeCheck3.typeOf(getConstraintExpression(KNOWN_BODY));

    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean -> ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void derivesTheFunctionTypeOfABodyWithACalcDefCall() throws IOException {
    var type = TypeCheck3.typeOf(getConstraintExpression(BOOLEAN_CALC_DEF + BODY_WITH_CALC_DEF));

    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean -> ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void acceptsABodyWithACalcDefCallAsForAllArgument() throws IOException {
    var type = TypeCheck3.typeOf(getConstraintExpression(BOOLEAN_CALC_DEF + BODY_WITH_TARGET));

    assertThat(type.printFullName()).isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void rejectsABodyWithAnIncompatibleCalcDefResult() throws IOException {
    var type = TypeCheck3.typeOf(getConstraintExpression(
      "calc def isValid { in input: ScalarValues.Boolean; return : ScalarValues.Integer; }"
        + BODY_WITH_TARGET));

    assertThat(type.isObscureType()).isTrue();
    assertThat(Log.getErrorCount()).isPositive();
  }

  private ASTExpression getConstraintExpression(String model) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).orElseThrow();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var constraint = (ASTConstraintUsage) ast.getSysMLElement(ast.sizeSysMLElements() - 1);
    return constraint.getExpression();
  }
}
