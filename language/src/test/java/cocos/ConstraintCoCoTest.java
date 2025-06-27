/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintCoCoTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/constraints";

  private SysMLv2Tool tool;

  @BeforeAll public static void init() {
    LogStub.init();
    OCLSymTypeRelations.init();
    SysMLv2Mill.init();
  }

  @BeforeEach public void reset() {
    Log.getFindings().clear();
    tool = new SysMLv2Tool();
    tool.init();
  }

  @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
  @ValueSource(strings = { "1_valid.sysml",
      // boolean operator with literals
      "2_valid.sysml", // resolve & compare ports
      "3_valid.sysml", // resolve & compare channels
      "4_valid.sysml", // stream snth
      "5_valid.sysml", // port::channel-syntax with comparison
      //"6_valid.sysml", // port::channel-syntax with literal
      //"7_valid.sysml", // INF literal
      "8_valid.sysml", // forall construct
      "9_valid.sysml", // constraint with literal
      //"10_valid.sysml", // attribute definition without port
      "11_valid.sysml", // stream length
      "12_valid.sysml", // constraint with parameter
      "13_valid.sysml", // OCL exists expression
      "14_valid.sysml", // StreamConstructor Expression
      "15_valid.sysml", //Times function for StreamConstructor Expression
      "16_valid.sysml", //Inftimes and takes function 
  })
  public void testValid(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest(name = "{index} - {0} does pass all checks w/o errors")
  @ValueSource(strings = { "1_invalid.sysml",
      // boolean operator with literals
      "2_invalid.sysml", // resolve & compare ports
      "3_invalid.sysml", // resolve & compare channels
      "4_invalid.sysml", // stream snth
      "5_invalid.sysml", // port::channel-syntax with comparison
      "6_invalid.sysml", // port::channel-syntax with literal
      "7_invalid.sysml", // INF literal
      "8_invalid.sysml", // forall construct
      "9_invalid.sysml", // constraint with literal
      //"10_invalid.sysml", // attribute definition without port
      "11_invalid.sysml", // stream length
      //"12_invalid.sysml", // constraint with parameter
      "13_invalid.sysml", // OCL exists expression
      "14_invalid.sysml", // StreamConstructor Expression
      "15_invalid.sysml", //Times function for StreamConstructor Expression
      "16_invalid.sysml", //Inftimes and takes function 
  })
  public void testInvalid(String modelName) throws IOException {
    var optAst = SysMLv2Mill.parser().parse(MODEL_PATH + "/" + modelName);
    assertThat(optAst).isPresent();

    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    assertFalse(Log.getFindings().isEmpty());
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
