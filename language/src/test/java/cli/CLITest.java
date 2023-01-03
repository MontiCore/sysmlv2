package cli;

import de.monticore.lang.sysmlv2.SysMLv2GeneratorTool;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;


public class CLITest {

  private static final String MODEL_PATH = "src/test/resources/cocos/actions";

  private SysMLv2Parser parser = SysMLv2Mill.parser();

  private SysMLv2GeneratorTool sysMLv2GeneratorTool = new SysMLv2GeneratorTool();

  @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
  @ValueSource(strings = {
      "0_valid.sysml" // example with action usage
      //    "1_valid.sysml", // example with control action usages
  })
  public void testValid(String modelName) throws IOException {
    var arg = new String[] { "-i " + MODEL_PATH + "/" + modelName, "-s " + "test.json" };
    sysMLv2GeneratorTool.run(arg);

  }
}


