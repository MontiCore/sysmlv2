package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintCoCoTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/constraints";

  @BeforeAll public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach public void reset() {
    SysMLv2Mill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    Log.getFindings().clear();
  }

  @Nested public class StateDef {

    @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
    @ValueSource(strings = {
        "1_valid.sysml",
        "2_valid.sysml",
        "3_valid.sysml",
        "4_valid.sysml",
        "5_valid.sysml",
        //"6_valid.sysml",
        //"7_valid.sysml",
        //"8_valid.sysml",
        "9_valid.sysml",
        //"10_valid.sysml",
        //"11_valid.sysml",
        "12_valid.sysml",
        "13_valid.sysml",
        "14_valid.sysml",
    })
    public void testValid(String modelName) throws IOException {
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);

      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        new SysMLv2Tool().completeSymbolTable(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo(new ConstraintIsBoolean());
        checker.checkAll(ast);

        assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
      }
      else {
        Assertions.fail("not parsable");
      }
    }

    @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
    @ValueSource(strings = {
        "1_invalid.sysml",
        "2_invalid.sysml",
        "3_invalid.sysml",
        "4_invalid.sysml",
        "5_invalid.sysml",
        "6_invalid.sysml",
        "7_invalid.sysml",
        //"8_invalid.sysml",
        "9_invalid.sysml",
        "10_invalid.sysml",
        //"11_invalid.sysml",
        "12_invalid.sysml",
        //"13_invalid.sysml",
        "14_invalid.sysml",
    })
    public void testInvalid(String modelName) throws IOException {
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);

      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        new SysMLv2Tool().completeSymbolTable(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo(new ConstraintIsBoolean());
        Log.enableFailQuick(false);
        checker.checkAll(ast);
        Log.enableFailQuick(true);

        assertFalse(Log.getFindings().isEmpty());
      }
      else {
        Assertions.fail("not parsable");
      }
    }
  }

}
