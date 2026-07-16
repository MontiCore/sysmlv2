/*package parser;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml._ast.ASTKerMLDocument;
import de.monticore.lang.kerml._parser.KerMLParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KerMLDataTypeLibParserTest {

  @BeforeAll
  public static void setUp(){
    Log.init();
    Log.enableFailQuick(false);
    KerMLMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Collections.kerml",
      "ScalarValues.kerml",
      "VectorValues.kerml"
  })
  public void testParseKernelDataTypeLibrary(String filename) throws IOException {
    String modelFile = "src/test/resources/KernelDataTypeLibrary/" + filename;
    KerMLParser parser = KerMLMill.parser();
    Optional<ASTKerMLDocument> ast = parser.parse(modelFile);


    assertFalse(parser.hasErrors(), "Parser threw Errors!");
    assertTrue(ast.isPresent(), "Ast generation failed!");
  }
}
*/
