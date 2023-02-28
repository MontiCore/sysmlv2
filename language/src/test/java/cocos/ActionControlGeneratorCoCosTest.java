/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.ActionControlGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionNameCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionSupertypes;
import de.monticore.lang.sysmlv2.visitor.ActionShortnotationVisitor;
import de.monticore.lang.sysmlv2.visitor.ActionSuccessionVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionControlGeneratorCoCosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/actions";


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
        "2_valid.sysml", // example with action usage
           })

    public void testValid(String modelName) throws IOException {
    }
    /**
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);

        SysMLv2Traverser sysMLv2Traverser = SysMLv2Mill.traverser();
        //sysMLv2Traverser.add4SysMLActions(new ActionShortnotationVisitor());
        //sysMLv2Traverser.handle(ast);
        sysMLv2Traverser.add4SysMLActions(new ActionSuccessionVisitor());
        sysMLv2Traverser.handle(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionGeneratorCoCos());
        checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionGeneratorCoCos());
        checker.addCoCo(new ActionControlGeneratorCoCos());
        checker.checkAll(ast);
        assertTrue(Log.getFindings().isEmpty());
      }
      else {
        Assertions.fail("not parsable");
      }
    }
     **/

  }

}
