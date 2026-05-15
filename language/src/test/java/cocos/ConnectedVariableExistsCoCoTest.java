package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConnectedVariableExistsCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectedVariableExistsCoCoTest {

  private static final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }

  @Test
  public void testValidConnection() throws IOException {
    String model =
      "part def Outer { port i: boolean;"
        + "attribute o: int;"
        + "connect o to i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedVariableExistsCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testInvalidConnection() throws IOException {
    String model =
        "part def Outer { "
            + "attribute o: int;"
            + "connect o to i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedVariableExistsCoCo());
    checker.checkAll(ast);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AD1");
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
