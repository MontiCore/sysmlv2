package symboltable;

import de.monticore.lang.sysmlexpressions._ast.ASTExistsExpression;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RequirementSymbolAdapterTest extends NervigeSymboltableTests {

  @BeforeAll
  static void setup() {
    Log.init();
  }

  @Test
  public void testRequirement() throws IOException {
    var as = process(
        "port def Integers { attribute val: int; }\n" +
        "part def P {\n" +
        "  port i: Integers;\n" +
        "  exhibit state S { attribute k: int; state Start; } }\n" +
        "requirement R {\n" +
        "  subject S;\n" +
        "  assert constraint { exists int a: i.val == <a, Tick> } }");

    var req = as.resolveRequirement("R");
    assertThat(req).isPresent();

    var requirement = req.get();
    assertThat(requirement.getConstraint()).isInstanceOf(ASTExistsExpression.class);

    assertThat(requirement.getSubject().getName()).isEqualTo("S");
  }

}
