package cocos.spesml;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.FlowCheckCoCo;
import de.monticore.lang.sysmlv2.cocos.PortDefinitionExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.StateDefCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpesMLTest {
  private static final String MODEL_PATH = "src/test/resources/cocos/doubleInverter/";

  private final SysMLv2Parser parser = new SysMLv2Parser();

  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
  }

  @Test
  void flowCheckCoCoTestValid() throws IOException {
    String modelPath = Paths.get(MODEL_PATH, "flowCheckCoCo", "valid", "1_valid.sysml").toString();
    Optional<ASTSysMLModel> ast = parser.parse(modelPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new FlowCheckCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void flowCheckCoCoTestInvalid() throws IOException {
    String modelPath = Paths.get(MODEL_PATH, "flowCheckCoCo", "invalid", "1_invalid.sysml").toString();
    Optional<ASTSysMLModel> ast = parser.parse(modelPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new FlowCheckCoCo());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void portDefinitionExistsTestInvalid() throws IOException {
    var model = "part def C;";
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new PortDefinitionExistsCoCo());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void portDefinitionExistsTestValid() throws IOException {
    var model = "port def A { in attribute b: B; } part def C { port a: A; }";
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new PortDefinitionExistsCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void stateDefinitionExistsTestInValid() throws IOException {
    var model = "port def A { in attribute b: B; } part def C { port a: A; }";
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new StateDefCoCo());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  void stateDefinitionExistsTestValid() throws IOException {
    var model = "part def B { state behavior : BAutomaton(); } ";
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new StateDefCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"doubleInverter.sysml", "inverter.sysml", "port.sysml"})
  void testDoubleInverterModelValid(String inputValue) throws IOException {
    String modelPath = Paths.get(MODEL_PATH, "model", inputValue).toString();
    Optional<ASTSysMLModel> ast = parser.parse(modelPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      st.runAdditionalCoCos(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new FlowCheckCoCo());
      checker.addCoCo(new StateDefCoCo());
      checker.addCoCo(new PortDefinitionExistsCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }
    else {
      Assertions.fail("AST is not present");
    }
  }

}

