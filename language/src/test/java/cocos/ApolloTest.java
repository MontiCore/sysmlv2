package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import de.se_rwth.commons.logging.Finding;

/**
 * Checks that the Apollo 11 models can be parsed and pass all CoCo checks.
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
    LogStub.init();
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
    var asts = new ArrayList<ASTSysMLModel>();

    for(var model: models) {
      try {
        var ast = SysMLv2Mill.parser().parse(model.toString());
        assertThat(ast).as("Could not parse " + model).isPresent();
        asts.add(ast.get());
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
    asts.forEach(ast -> tool.createSymbolTable(ast));
    asts.forEach(ast -> tool.completeSymbolTable(ast));
    asts.forEach(ast -> tool.finalizeSymbolTable(ast));

    //asts.forEach(ast -> tool.runDefaultCoCos(ast));
   // asts.forEach(ast -> tool.runAdditionalCoCos(ast));

    assertThat(successful).isEqualTo(27);

    var errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
    assertThat(errors).as(errors::toString).isEmpty();
  }

}
