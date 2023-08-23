package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Versucht die offiziellen Modelle zu parsen
 */
public class OfficialParserTest {

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

  @Disabled
  @Test
  public void testParse() throws IOException {
    var models = Files.walk(Path.of("src/test/resources/official"))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(57);

    for(var model: models) {
      var ast = parser.parse(model.toString());
      assertThat(ast).isPresent();
      assertThat(Log.getFindings().isEmpty());
      assertThat(!parser.hasErrors());
    }
  }

}
