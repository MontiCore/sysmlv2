package symboltable;

import de.monticore.lang.componentconnector.SerializationUtil;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract public class NervigeSymboltableTests extends SerializationUtil {

  protected static SysMLv2Tool tool;

  @BeforeAll
  public static void _setup() {
    tool = new SysMLv2Tool();
  }

  @BeforeEach
  public void init() {
    // Important: Resets everything, including crude DeSer setup!
    tool.init();
    LogStub.init();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  /** Erledigt das nervige Geraffel */
  protected ISysMLv2ArtifactScope process(String model) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var as = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    return as;
  }

}
