/* (c) https://github.com/MontiCore/monticore */
package prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
public class PrettyPrinterTest {

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
      "parallel_states.sysml",
      "actions.sysml",
      "items.sysml",
      "assert.sysml",
      "constraints.sysml",
      "requirements.sysml",
      "streams.sysml",
      "refinement.sysml",
      "cardinalities.sysml",
      "connections.sysml",
      "collections.sysml",
      "StateDecomposition1.sysml",
      "FlowConectionInterfaceExample.sysml",
      "StateActions.sysml",
      "ConditionalSuccessionExample-1.sysml"
  })
  public void testPrintingModels(String modelName) throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse(MODEL_PATH  + "/" + modelName);
    assertFalse(parser.hasErrors(), "Parsing should not have failed");
    assertTrue(ast.isPresent(), "The AST should have been created");

    String ppm = SysMLv2Mill.prettyPrint(ast.get(), true);
    System.out.println(ppm);
//    assertTrue(!ppm.isEmpty(), "The printed ast should be available");
//
//    SysMLv2Mill.parser().parse_String(ppm);
//    assertFalse(parser.hasErrors(), "Parsing of printed ast should not have failed");
//    assertTrue(ast.isPresent(), "The AST of printed ast should have been created");
  }

}
