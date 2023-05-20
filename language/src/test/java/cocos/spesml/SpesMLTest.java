package cocos.spesml;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.FlowCheckCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpesMLTest {
  private static final String MODEL_PATH = "src/test/resources/cocos/doubleInverter/";
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init(){
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset(){
    Log.getFindings().clear();
  }

  @Test
  void FlowCheckCoCoTestValid() throws IOException {
    String model = Paths.get(MODEL_PATH, "flowCheckCoCo", "valid", "1_valid.sysml").toString();
    Optional<ASTSysMLModel> ast = parser.parse(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      st.runAdditionalCoCos(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new FlowCheckCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void FlowCheckCoCoTestInvalid() throws IOException {
    String model = Paths.get(MODEL_PATH, "flowCheckCoCo", "invalid", "1_invalid.sysml").toString();
    Optional<ASTSysMLModel> ast = parser.parse(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      st.runAdditionalCoCos(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new FlowCheckCoCo());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }
}
