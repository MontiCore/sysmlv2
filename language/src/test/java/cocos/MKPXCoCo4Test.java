/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.MKPX_CoCo4;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MKPXCoCo4Test {

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
  public class PortExistenceTests {
    @Test
    public void testValid() throws IOException {
      String validModel =
          "part def A { port p; }"
        + "part def B { port q: ~; }"
        + "part def System {"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect a.p to b.q;"
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(validModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo4());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalid() throws IOException {
      String invalidModel =
          "part def A { port p; }"
        + "part def B { port q: ~; }"
        + "part def System {"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect a.wrongName to b.q;"
        + "}";

      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String(invalidModel).get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTConnectionUsageCoCo) new MKPX_CoCo4());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertTrue(Log.getFindings().stream()
              .anyMatch(f -> f.getMsg().contains("0xMKPX04")));
    }
  }
}
