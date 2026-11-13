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

public class KerMLSpecAnnexAExamplesParserTest {

  @BeforeAll
  public static void setUp() {
    Log.init();
    Log.enableFailQuick(false);
    KerMLMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      // "A-2-Atoms.kerml",
      "A-2-ModelingInstances.kerml",
      "A-3-2-WithoutConnectors.kerml",
      "A-3-3-OneToOneConnectors.kerml",
      "A-3-4-OneToUnrestrictedConnectors.kerml",
      "A-3-5-TimingForStructures.kerml",
      "A-3-6-Sequences.kerml",
      "A-3-7-DecisionsAndMerges.kerml",
      "A-3-8-ChangingFeatureValues.kerml"
  })
  public void testParseExample(String filename) throws IOException {
    String modelFile = "src/test/resources/KerMLSpecAnnexAExamples/" + filename;
    KerMLParser parser = KerMLMill.parser();
    Optional<ASTKerMLModel> ast = parser.parse(modelFile);

    assertFalse(parser.hasErrors(), "Parser threw errors for " + modelFile);
    assertTrue(ast.isPresent(), "AST generation failed for " + modelFile);
  }
}
