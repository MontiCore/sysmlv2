/* (c) https://github.com/MontiCore/monticore */
package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks official training and validation models
 */
public class OfficialParserTest {

  static final String officialExamples = "src/test/resources/official";

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
  public void testIfActionUsage() throws IOException {
    var model = "if i < 0 { assign i := 0; } else if i == 0 { assign i := 1; } else { assign i := 2; }";
    var ast = SysMLv2Mill.parser().parse_StringIfActionUsage(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testEmptyExpressionDirectly() throws IOException {
    var model = "( //* ... */ )";
    var ast = SysMLv2Mill.parser().parse_StringSysMLEmptyExpression(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testEmptyExpression() throws IOException {
    var model = "( //* ... */ )";
    var ast = SysMLv2Mill.parser().parse_StringExpression(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testParseAll() throws IOException {
    var models = Files.walk(Path.of(officialExamples))
        .filter(p -> p.toFile().getName().endsWith(".sysml"))
        .collect(Collectors.toList());

    assertThat(models).hasSize(244);
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

    //System.out.println("Success rate: " + successful + "/" + 243 + " (" + lines + " findings)");
    assertThat(successful).isEqualTo(244);
    assertThat(Log.getFindings()).isEmpty();
  }

}
