/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintCoCoTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/constraints";

  @BeforeAll public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach public void reset() {
    SysMLv2Mill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    SysMLv2Mill.addStreamType();
    Log.getFindings().clear();
  }

  // TODO @Marc, question: Warum ist der Test-Code in eine Nested-Klasse mit dem Namen gewandert?
  @Nested public class StateDef {

    @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
    @ValueSource(strings = {
        "1_valid.sysml", // boolean operator with literals
        "2_valid.sysml", // resolve & compare ports
        "3_valid.sysml", // resolve & compare channels
        "4_valid.sysml", // stream snth
        "5_valid.sysml", // port::channel-syntax with comparison
        //"6_valid.sysml", // port::channel-syntax with literal
        //"7_valid.sysml", // INF literal
        //"8_valid.sysml", // forall construct
        "9_valid.sysml", // constraint with literal
        //"10_valid.sysml", // attribute definition without port
        "11_valid.sysml", // stream length
        "12_valid.sysml", // constraint with parameter
    })
    public void testValid(String modelName) throws IOException {
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);

      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        new SysMLv2Tool().completeSymbolTable(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo(new ConstraintIsBoolean());
        checker.checkAll(ast);

        assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
      }
      else {
        Assertions.fail("not parsable");
      }
    }

    @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
    @ValueSource(strings = {
        "1_invalid.sysml", // boolean operator with literals
        "2_invalid.sysml", // resolve & compare ports
        "3_invalid.sysml", // resolve & compare channels
        "4_invalid.sysml", // stream snth
        "5_invalid.sysml", // port::channel-syntax with comparison
        "6_invalid.sysml", // port::channel-syntax with literal
        "7_invalid.sysml", // INF literal
        //"8_invalid.sysml", // forall construct
        "9_invalid.sysml", // constraint with literal
        //"10_invalid.sysml", // attribute definition without port
        "11_invalid.sysml", // stream length
        //"12_invalid.sysml", // constraint with parameter
    })
    public void testInvalid(String modelName) throws IOException {
      var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);

      if(optAst.isPresent()) {
        ASTSysMLModel ast = optAst.get();

        SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
        new SysMLv2Tool().completeSymbolTable(ast);

        var checker = new SysMLv2CoCoChecker();
        checker.addCoCo(new ConstraintIsBoolean());
        Log.enableFailQuick(false);
        checker.checkAll(ast);
        Log.enableFailQuick(true);

        assertFalse(Log.getFindings().isEmpty());
      }
      else {
        Assertions.fail("not parsable");
      }
    }
  }

}
