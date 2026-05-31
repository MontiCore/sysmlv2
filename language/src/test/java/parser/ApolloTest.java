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
 * Checks that the Apollo_11 can be parsed
 * https://github.com/airbus/apollo-11-sysml-v2/tree/main
 */
public class ApolloTest {
  private static final String APOLLO11_MODEL_PATH = "src/test/resources/apollo_11";

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
  public void testParseApollo11Models() throws IOException {
    var models = Files.walk(Path.of(APOLLO11_MODEL_PATH))
        .filter(p -> p.toString().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(27);
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

    assertThat(successful).isEqualTo(27);
    assertThat(Log.getFindings()).isEmpty();
  }

}
