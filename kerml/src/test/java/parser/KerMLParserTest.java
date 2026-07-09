package parser;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml._ast.ASTKerMLDocument;
import de.monticore.lang.kerml._parser.KerMLParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KerMLParserTest {

  @BeforeAll
  public static void init(){
    KerMLMill.init();
  }

  @Test
  public void testParseTargetSentence() throws IOException {
    String modelFile = "src/test/resources/parser/Collection.kerml";
    KerMLParser parser = KerMLMill.parser();
    Optional<ASTKerMLDocument> ast = parser.parse(modelFile);

    assertFalse(parser.hasErrors(), "Parser threw Errors!");
    assertTrue(ast.isPresent(), "Ast generation failed!");
  }
}
