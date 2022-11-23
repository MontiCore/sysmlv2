package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.PartSupertypes;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PartCoCosTest {

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
  public class StateDef {

    @Test
    public void testValid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("part def A; part def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTPartDefCoCo) new PartSupertypes());
      checker.addCoCo((SysMLPartsASTPortDefCoCo) new PartSupertypes());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("part def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTPartDefCoCo) new PartSupertypes());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertFalse(Log.getFindings().isEmpty());
    }
  }


}
