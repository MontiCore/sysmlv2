package cocos;

import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.ActionGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionNameCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionSupertypes;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionCoCosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/actions";

  private SysMLv2Parser parser = SysMLv2Mill.parser();

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    Log.getFindings().clear();
    Log.enableFailQuick(true);
  }

  @Nested
  public class ActionDef {

    @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
    @ValueSource(strings = {
        "0_valid.sysml", // example with action usage
        "1_valid.sysml", // example with control action usages
    })
    public void testValid(String modelName) throws IOException {
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionSupertypes());
        checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionNameCoCos());

        checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionGeneratorCoCos());

        checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionSupertypes());
        checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionNameCoCos());
        checker.checkAll(ast);
        assertTrue(Log.getFindings().isEmpty());
      }
      else {
        Assertions.fail("not parsable");
      }
    }

    @Test
    public void testInvalid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("action def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionSupertypes());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertFalse(Log.getFindings().isEmpty());
    }
  }

}
