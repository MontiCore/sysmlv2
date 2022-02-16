package schrotttests;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintsTest {
  /**
   * Checks whether "constraints/Valid.sysml" is parsable.
   *
   * @param model the models to be checked. They are specified in @ValueSource.
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "Valid"
  })
  public void testParse(String model) throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser().parse(
        "src/test/resources/constraints/" + model + ".sysml");
    assertThat(optAst).isNotEmpty();
  }
}
