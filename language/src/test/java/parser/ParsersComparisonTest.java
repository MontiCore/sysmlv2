package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.omg.sysml.interactive.SysMLInteractive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParsersComparisonTest {
  private static final String MODEL_PATH = "src/omgTest/resources/parser";

  private SysMLv2Parser parser = SysMLv2Mill.parser();

  // SysMLInteractive is a singleton; keep one reference
  private static SysMLInteractive official;

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();

    official = SysMLInteractive.getInstance();
    official.setVerbose(false);
  }

  @BeforeEach
  public void reset() {
    parser.setError(false);
  }

  @ParameterizedTest(name = "{index} - {0} does parse w/o errors (MontiCore + official)")
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
      "streamsFilter.sysml",
      "refinement.sysml",
      "cardinalities.sysml",
      "connections.sysml",
      "collections.sysml",
      "StateDecomposition1.sysml",
      "FlowConectionInterfaceExample.sysml",
      "StateActions.sysml",
      "ConditionalSuccessionExample-1.sysml"
  })
  public void testParsingModels(String modelName) throws IOException {
    Path modelPath = Path.of(MODEL_PATH, modelName);

    // 1) MontiCore parser (existing behavior)
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse(modelPath.toString());
    assertFalse(parser.hasErrors(), "MontiCore parsing should not have failed");
    assertTrue(ast.isPresent(), "MontiCore AST should have been created");

    // 2) Official OMG parser
    String input = Files.readString(modelPath);
    assertDoesNotThrow(
        () -> official.parse(input),
        () -> "Official OMG parse() failed for " + modelName + " (" + modelPath + ")"
    );

  }
}
