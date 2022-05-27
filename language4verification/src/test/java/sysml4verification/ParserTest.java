package sysml4verification;

import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

  private static final String MODEL_PATH = "src/test/resources/sysml4verification/parser/";

  @ParameterizedTest(name = "{index} - {0} does parse w/o errors")
  @ValueSource(strings = {
      "DLUFv7.sysml",
      "DLUFv8.sysml",
      "requirement_state_invariant.sysml"
  })
  public void testParsingModels(String path) throws IOException {
    Path model = Paths.get(MODEL_PATH + path);

    SysML4VerificationMill.init();
    SysML4VerificationParser parser = SysML4VerificationMill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
