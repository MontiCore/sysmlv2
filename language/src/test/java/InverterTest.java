import de.monticore.lang.sysml4verification.SysML4VerificationTool;
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

public class InverterTest {

  static final String FOLDER = "src/test/resources/inverter";

  static SysML4VerificationTool tool;

  @BeforeAll
  public static void setup() {
    tool = new SysML4VerificationTool();
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

}
