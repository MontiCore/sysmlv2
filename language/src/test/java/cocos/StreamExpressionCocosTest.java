package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamExpressionCocosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/constraints";

  private SysMLv2Tool tool;

  @BeforeAll public static void init() {
    LogStub.init();
    OCLSymTypeRelations.init();
    SysMLv2Mill.init();
  }

  @BeforeEach public void reset() {
    Log.getFindings().clear();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @ParameterizedTest()
  @ValueSource(strings = {
      "15_valid.sysml",
  })
  public void teststreamRepeat(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);
    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
