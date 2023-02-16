/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.StateGeneratorCoCo;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;
import de.monticore.lang.sysmlv2.cocos.SuccessionCoCo;
import de.monticore.lang.sysmlv2.visitor.ActionSuccessionVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateCoCosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/states/0_valid.sysml";

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
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH);
      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();
        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        ActionSuccessionVisitor actionSuccessionVisitor = new ActionSuccessionVisitor();
        SysMLv2Traverser sysMLv2Traverser = SysMLv2Mill.traverser();
        sysMLv2Traverser.add4SysMLActions(actionSuccessionVisitor);
        sysMLv2Traverser.handle(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
        checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateSupertypes());
        checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateGeneratorCoCo());
        checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateGeneratorCoCo());

        checker.addCoCo(new SuccessionCoCo());
        checker.checkAll(ast);
        assertTrue(Log.getFindings().isEmpty());
      }
    }

    @Test
    public void testInvalid() throws IOException {
      ASTSysMLModel ast = SysMLv2Mill.parser().parse_String("state def B: A;").get();
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
      Log.enableFailQuick(false);
      checker.checkAll(ast);
      assertFalse(Log.getFindings().isEmpty());
    }
  }

}
