package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApolloTest {
  private static final String APOLLO11_MODEL_PATH = "src/test/resources/Apollo_11";

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParseApollo11Models() throws IOException {
    var models = Files.walk(Path.of(APOLLO11_MODEL_PATH))
        .filter(p -> p.toString().endsWith(".sysml"))
        .sorted()
        .collect(Collectors.toList());

    assertFalse(models.isEmpty(), "No .sysml files found in " + APOLLO11_MODEL_PATH);

    StringBuilder failures = new StringBuilder();
    int successful = 0;

    for (Path model : models) {
      SysMLv2Parser parser = SysMLv2Mill.parser();
      parser.setError(false);
      Log.clearFindings();

      Optional<ASTSysMLModel> ast = parser.parse(model.toString());

      if (parser.hasErrors() || ast.isEmpty()) {
        failures.append("\n").append(model);
      }
      else {
        successful++;
      }

      Log.clearFindings();
    }

    System.out.println("Apollo 11 parser test result: " + successful + " / " + models.size() + " files parsed successfully.");
    assertTrue(failures.length()==0, "Some Apollo 11 SysML files could not be parsed. "
            + successful + " / " + models.size() + " files parsed successfully.\nFailed files: " + failures);
  }
}
