/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlv2.cocos.MKPX_CoCo5;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MKPXCoCo5Test {

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
  public class OutputConnectionTests {
    @Test
    public void testValid() throws IOException {
      String validModel =
          "part def A { port out: int; }"
        + "part def B { port in: ~int; }"
        + "part def System {"
        +   "port sysOut: int;"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect a.out to b.in;"          // (Sub) Output -> (Sub) Input
        +   "connect a.out to sysOut;"        // (Sub) Output -> (main) Output
        + "}";
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo5());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty(),
          "Expected no errors for valid output connections");
    }

    @Test
    public void testInvalidSubOutToSubOut() throws IOException {
      String invalidModel =
          "part def A { port out: int; }"
        + "part def B { port out: int; }"
        + "part def System {"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect a.out to b.out;"         // (Sub) Output -> (Sub) Output
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo5());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
              .anyMatch(f -> f.getMsg().contains("0xMKPX05")));
    }

    @Test
    public void testInvalidSubOutToMainIn() throws IOException {
      String invalidModel =
          "part def A { port out: int; }"
        + "part def System {"
        +   "port sysIn: ~int;"
        +   "part a: A;"
        +   "connect a.out to sysIn;"         // (Sub) Output -> (main) Input
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo5());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
          .anyMatch(f -> f.getMsg().contains("0xMKPX05")));
    }
  }
}
