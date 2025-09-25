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
 * Checks that the ESA Comet Interceptor Case Study works with our parser
 */
public class CometInterceptorTest {

  static final String FOLDER = "src/test/resources/esa_comet";

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
    var models = Files.walk(Path.of(FOLDER))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(1);

    Log.enableFailQuick(false);
    for(var model: models) {
      try {
        var ast = tool.parse(model.toString());
      } catch (Exception e) {
        // Erstmal nur messen
      }
      finally {
        //Log.clearFindings();
      }
    }

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testProcessAll() throws IOException {
    var models = Files.walk(Path.of(FOLDER))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(1);

    Log.enableFailQuick(false);

    var asts = models.stream()
        .map(m -> tool.parse(m.toString()))
        .collect(Collectors.toList());

    asts.forEach(it -> tool.createSymbolTable(it));
    asts.forEach(it -> tool.completeSymbolTable(it));
    asts.forEach(it -> tool.finalizeSymbolTable(it));

    asts.forEach(it -> tool.runDefaultCoCos(it));

    assertThat(Log.getErrorCount()).isEqualTo(0);
    //assertThat(Log.getFindings()).isEmpty();
  }

}
