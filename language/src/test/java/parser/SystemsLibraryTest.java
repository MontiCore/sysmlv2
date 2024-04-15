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

public class SystemsLibraryTest {

  static final String systemsLibrary = "src/main/resources/Systems Library";

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
    var models = Files.walk(Path.of(systemsLibrary))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(20);
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

    //System.out.println("Success rate: " + successful + "/" + 20 + " (" + lines + " findings)");
    assertThat(successful).isEqualTo(20);
    assertThat(Log.getFindings()).isEmpty();
  }

}
