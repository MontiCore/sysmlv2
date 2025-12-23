/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.SubcomponentOutputConnectionDirectionCoCo;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubcomponentOutputConnectionDirectionCoCoTest {

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
          "port def OutPort { out attribute data: int; }"
              + "port def InPort { in attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port input: InPort; }"
              + "part def C { port output: OutPort; }"
              + "part def System {"
              +   "port sysOutput: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "part c: C;"
              +   "connect a.output to b.input;"          // (Sub) Output -> (Sub) Input
              +   "connect c.output to sysOutput;"        // (Sub) Output -> (main) Output
              + "}";
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new SubcomponentOutputConnectionDirectionCoCo());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testValidConjugatedModel() throws IOException {
      String validModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port input: ~OutPort; }"
              + "part def C { port output: OutPort; }"
              + "part def System {"
              +   "port sysOutput: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "part c: C;"
              +   "connect a.output to b.input;"          // (Sub) Output -> (Sub) Input
              +   "connect c.output to sysOutput;"        // (Sub) Output -> (main) Output
              + "}";
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new SubcomponentOutputConnectionDirectionCoCo());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalidSubOutToSubOut() throws IOException {
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port output: OutPort; }"
              + "part def System {"
              +   "part a: A;"
              +   "part b: B;"
              +   "connect a.output to b.output;"         // (Sub) Output -> (Sub) Output
              + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new SubcomponentOutputConnectionDirectionCoCo());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
              .anyMatch(f -> f.getMsg().contains("0x10AA5")));
    }

    @Test
    public void testInvalidSubOutToMainInConjugatedModel() throws IOException {
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def System {"
              +   "port sysInput: ~OutPort;"
              +   "part a: A;"
              +   "connect a.output to sysInput;"         // (Sub) Output -> (main) Input
              + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new SubcomponentOutputConnectionDirectionCoCo());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
          .anyMatch(f -> f.getMsg().contains("0x10AA5")));
    }

    @Test
    public void testInvalidSubOutToMainIn() throws IOException {
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "port def InPort { in attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def System {"
              +   "port sysInput: InPort;"
              +   "part a: A;"
              +   "connect a.output to sysInput;"         // (Sub) Output -> (main) Input
              + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new SubcomponentOutputConnectionDirectionCoCo());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
          .anyMatch(f -> f.getMsg().contains("0x10AA5")));
    }
  }
}
