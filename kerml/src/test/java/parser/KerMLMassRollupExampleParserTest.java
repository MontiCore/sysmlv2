package parser;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml._ast.ASTKerMLModel;
import de.monticore.lang.kerml._parser.KerMLParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KerMLMassRollupExampleParserTest {

  @BeforeAll
  public static void setUp() {
    Log.init();
    Log.enableFailQuick(false);
    KerMLMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "MassRollup_1.kerml",
      "MassRollup_2.kerml",
      "Vehicles_1.kerml",
      "Vehicles_2.kerml",
      "Vehicles_3.kerml"
  })
  public void testParseExample(String filename) throws IOException {
    String modelFile = "src/test/resources/MassRoll-upExample/" + filename;
    KerMLParser parser = KerMLMill.parser();
    Optional<ASTKerMLModel> ast = parser.parse(modelFile);

    assertFalse(parser.hasErrors(), "Parser threw errors for " + modelFile);
    assertTrue(ast.isPresent(), "AST generation failed for " + modelFile);
  }
}
