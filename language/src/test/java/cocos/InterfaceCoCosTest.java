package cocos;

import de.monticore.lang.sysmlinterfaces._cocos.SysMLInterfacesASTInterfaceDefCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.InterfaceSupertypes;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterfaceCoCosTest {

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
  public class InterfaceDef {

    @Test
    public void testValid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("interface def A; interface def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLInterfacesASTInterfaceDefCoCo) new InterfaceSupertypes());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testInvalid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("interface def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLInterfacesASTInterfaceDefCoCo) new InterfaceSupertypes());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertFalse(Log.getFindings().isEmpty());
    }
  }

}
