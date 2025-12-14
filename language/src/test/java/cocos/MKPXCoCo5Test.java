/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
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
          "part def A { port output: int; }"
        + "part def B { port input: ~int; }"
        + "part def C { port output: int; }"
        + "part def System {"
        +   "port sysOutput: int;"
        +   "part a: A;"
        +   "part b: B;"
        +   "part c: C;"
        +   "connect a.output to b.input;"          // (Sub) Output -> (Sub) Input
        +   "connect c.output to sysOutput;"        // (Sub) Output -> (main) Output
        + "}";
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo5());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalidSubOutToSubOut() throws IOException {
      String invalidModel =
          "part def A { port output: int; }"
        + "part def B { port output: int; }"
        + "part def System {"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect a.output to b.output;"         // (Sub) Output -> (Sub) Output
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
          "part def A { port output: int; }"
        + "part def System {"
        +   "port sysInput: ~int;"
        +   "part a: A;"
        +   "connect a.output to sysInput;"         // (Sub) Output -> (main) Input
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
