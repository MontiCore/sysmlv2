package typecheck;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SysMLBodyExpressionInferenceTest {
  private SysMLv2Tool tool;

  @BeforeEach
  void setUp() {
    LogStub.init();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @Disabled
  @Test
  void infersBodyParameterTypeFromForAll() throws IOException {
    var model = completeModel("attribute values: ScalarValues.Boolean[*];"
      + "constraint { ControlFunctions::forAll(values, { in value; value }) }");

    assertThat(typeOfConstraint(model, 1)).isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void derivesBodyAndCallTypesWithExplicitParameterType() throws IOException {
    var model = completeModel("attribute values: ScalarValues.Boolean[*];"
      + "constraint { { in value: ScalarValues.Boolean; value } }"
      + "constraint { ControlFunctions::forAll(values, { in value: ScalarValues.Boolean; value }) }");

    assertThat(typeOfConstraint(model, 1)).isEqualTo("ScalarValues.Boolean -> ScalarValues.Boolean");
    assertThat(typeOfConstraint(model, 2)).isEqualTo("ScalarValues.Boolean");
    assertThat(Log.getFindings()).isEmpty();
  }

  private String typeOfConstraint(ASTSysMLModel model, int index) {
    var constraint = (ASTConstraintUsage) model.getSysMLElement(index);
    return TypeCheck3.typeOf(constraint.getExpression()).printFullName();
  }

  private ASTSysMLModel completeModel(String source) throws IOException {
    var model = SysMLv2Mill.parser().parse_String(source).orElseThrow();
    tool.createSymbolTable(model);
    tool.completeSymbolTable(model);
    tool.finalizeSymbolTable(model);
    return model;
  }
}
