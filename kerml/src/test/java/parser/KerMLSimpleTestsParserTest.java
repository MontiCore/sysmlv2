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

public class KerMLSimpleTestsParserTest {

  @BeforeAll
  public static void setUp() {
    Log.init();
    Log.enableFailQuick(false);
    KerMLMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      // "ArgumentResolution.kerml",
      // "Associations.kerml",
      // "Behaviors.kerml",
      "Circular.kerml",
      "Classes.kerml",
      // "Classifications.kerml",
      "Classifiers.kerml",
      // "Comments.kerml",
      "Conjugation.kerml",
      // "Connectors.kerml",
      // "Dependencies.kerml",
      "Expansion.kerml",
      // "Expressions.kerml",
      // "FeatureChains.kerml",
      "FeatureInheritance.kerml",
      // "Features.kerml",
      // "Filtering.kerml",
      // "Imports.kerml",
      "Inheritance.kerml",
      // "Inverses.kerml",
      // "MetadataTest.kerml",
      "Redefinition.kerml",
      "Scoping.kerml",
      // "TextualRepresentation.kerml",
      // "Types.kerml"
  })
  public void testParseExample(String filename) throws IOException {
    String modelFile = "src/test/resources/SimpleTests/" + filename;
    KerMLParser parser = KerMLMill.parser();
    Optional<ASTKerMLModel> ast = parser.parse(modelFile);

    assertFalse(parser.hasErrors(), "Parser threw errors for " + modelFile);
    assertTrue(ast.isPresent(), "AST generation failed for " + modelFile);
  }
}
