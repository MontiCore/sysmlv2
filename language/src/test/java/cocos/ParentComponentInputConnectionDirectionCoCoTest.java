/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.ParentComponentInputConnectionDirectionCoCo;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParentComponentInputConnectionDirectionCoCoTest {

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
  public class InputConnectionTests {
    @Test
    public void testValid() throws IOException {
      String validModel =
          "port def InPort { in attribute data: int; }"
        + "port def OutPort { out attribute data: int; }"
        + "part def A { port input: InPort; }"
        + "part def B { port output: OutPort; }"
        + "part def System {"
        +   "port sysIn: InPort;"
        +   "port sysInAnother: InPort;"
        +   "port sysOut: OutPort;"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect sysIn to a.input;"
        +   "connect sysInAnother to sysOut;"
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new ParentComponentInputConnectionDirectionCoCo());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testValidConjugatedModel() throws IOException {
      String validModel =
          "port def InPort { in attribute data: int; }"
              + "part def A { port input: InPort; }"
              + "part def B { port output: ~InPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "port sysInAnother: InPort;"
              +   "port sysOut: ~InPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "connect sysIn to a.input;"
              +   "connect sysInAnother to sysOut;"
              + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new ParentComponentInputConnectionDirectionCoCo());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalid() throws IOException {
      String invalidModel =
          "port def InPort { in attribute data: int; }"
        + "port def OutPort { out attribute data: int; }"
        + "part def A { port output: OutPort; }"
        + "part def System {"
        +   "port sysIn: InPort;"
        +   "part a: A;"
        +   "connect sysIn to a.output;"
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new ParentComponentInputConnectionDirectionCoCo());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
              .anyMatch(f -> f.getMsg().contains("0x10AA6")));
    }

    @Test
    public void testInvalidConjugatedModel() throws IOException {
      String invalidModel =
          "port def InPort { in attribute data: int; }"
              + "part def A { port output: ~InPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "part a: A;"
              +   "connect sysIn to a.output;"
              + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new ParentComponentInputConnectionDirectionCoCo());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
          .anyMatch(f -> f.getMsg().contains("0x10AA6")));
    }
  }
}
