package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testet, ob von uns gebrauchte Modelle geparst werden können
 */
public class ParserTest {

  private static final String MODEL_PATH = "src/test/resources/parser";

  private SysMLv2Parser parser = SysMLv2Mill.parser();

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    parser.setError(false);
  }

  @ParameterizedTest(name = "{index} - {0} does parse w/o errors")
  @ValueSource(strings = {
      "packages.sysml",
      "imports.sysml",
      "ports.sysml",
      "parts.sysml",
      "states.sysml",
      "actions.sysml",
      "items.sysml",
      "assert.sysml",
      "constraints.sysml",
      "requirements.sysml",
      "streams.sysml",
      "refinement.sysml",
      "cardinalities.sysml",
      "connections.sysml",
      "StateDecomposition1.sysml",
      "FlowConectionInterfaceExample.sysml",
      "StateActions.sysml",
      "ConditionalSuccessionExample-1.sysml"
  })
  public void testParsingModels(String modelName) throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse(MODEL_PATH  + "/" + modelName);
    assertFalse(parser.hasErrors(), "Parsing should not have failed");
    assertTrue(ast.isPresent(), "The AST should have been created");
  }

}
