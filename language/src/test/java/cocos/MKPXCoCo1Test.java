/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.MKPX_CoCo1;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MKPXCoCo1Test {

  private static final String MODEL_PATH = "src/test/resources/parser";

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
  public class MKPXCoCo1Tests {
    @Test
    public void testValid() throws IOException {
      String validModel =
            "part def SubComponent1;"
          + "part def SubComponent2;"
          + "part def MainComponent{"
          +   "part subcomp1: SubComponent1;"
          +   "part subcomp2: SubComponent2;"
          + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTPartUsageCoCo) new MKPX_CoCo1());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalid() throws IOException {
      String invalidModel =
            "part def SubComponent1;"
          + "part def MainComponent{"
          +   "part subcomp1: SubComponent1;"
          +   "part subcomp2: UndefinedComponent;"
          + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTPartUsageCoCo) new MKPX_CoCo1());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
          .anyMatch(f -> f.getMsg().contains("0xMKPX01")));
    }
  }

}
