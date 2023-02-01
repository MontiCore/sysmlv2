package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.AttributeGeneratorCoCos;
import de.monticore.lang.sysmlv2.visitor.PartsTransitiveVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeGeneratorCoCosTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/attribute";

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

  @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
  @ValueSource(strings = {
      "0_valid.sysml" // example for redefinition
  })
  public void testValid(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    if(optAst.isPresent()) {
      ASTSysMLModel ast = optAst.get();

      SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
      SysMLv2Traverser sysMLv2Traverser = SysMLv2Mill.traverser();
      sysMLv2Traverser.add4SysMLParts(new PartsTransitiveVisitor());
      sysMLv2Traverser.handle(ast);
      var checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new AttributeGeneratorCoCos());
      checker.addCoCo((SysMLPartsASTAttributeUsageCoCo) new AttributeGeneratorCoCos());
      checker.checkAll(ast);
      assertTrue(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("not parsable");
    }
  }

}
