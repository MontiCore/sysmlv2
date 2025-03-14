package parser;

import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that the ESA SysML Solution can be parsed
 * https://essr.esa.int/project/esa-sysml-solution
 */
public class EsaSysMLSolutionTest {

  static final String esaProfile = "src/test/resources/esa";

  static SysMLv2Tool tool;

  @BeforeAll
  public static void setup() {
    tool = new SysMLv2Tool();
  }

  @BeforeEach
  public void init() {
    tool.init();
    Log.init();
  }

  @Test
  public void testParseAll() throws IOException {
    var models = Files.walk(Path.of(esaProfile))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(9);
    var successful = 0;
    var lines = 0;

    Log.enableFailQuick(false);
    for(var model: models) {
      try {
        var ast = tool.parse(model.toString());
        if(Log.getFindings().isEmpty()) {
          successful++;
        }
      } catch (Exception e) {
        // Erstmal nur messen
      }
      finally {
        lines += Log.getFindingsCount();
        Log.clearFindings();
      }
    }

    assertThat(successful).isEqualTo(9);
    assertThat(Log.getFindings()).isEmpty();
  }

}
