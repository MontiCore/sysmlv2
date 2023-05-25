package cocos.spesml;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.FlowCheckCoCo;
import de.monticore.lang.sysmlv2.cocos.PortDefinitionExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.StateDefCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
  public void reset(){
    Log.getFindings().clear();
  }

  @Test
  void flowCheckCoCoTestValid() throws IOException {
    var modelPath = Paths.get(MODEL_PATH, "flowCheckCoCo", "valid", "1_valid.sysml").toString();
    var ast = parser.parse(modelPath);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new FlowCheckCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(0);
  }

  @Test
  void flowCheckCoCoTestInvalid() throws IOException {
    var modelPath = Paths.get(MODEL_PATH, "flowCheckCoCo", "invalid", "1_invalid.sysml").toString();
    var ast = parser.parse(modelPath);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new FlowCheckCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(3);
    assertThat(findings.get(0).getMsg()).contains("0xA70001");
    assertThat(findings.get(1).getMsg()).contains("0xA70001");
    assertThat(findings.get(2).getMsg()).contains("0xA70001");
  }

  @Test
  void portDefinitionExistsTestValid() throws IOException {
    var model = "port def A { in attribute b: B; } part def C { port a: A; }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new PortDefinitionExistsCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(0);
  }

  @Test
  void portDefinitionExistsTestInvalid() throws IOException {
    var model = "part def C { }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new PortDefinitionExistsCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(1);
    assertThat(findings.get(0).getMsg()).contains("0xA70002");
  }

  @Test
  void stateDefinitionExistsTestValid() throws IOException {
    var model = "part def B { state behavior : BAutomaton(); } ";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new StateDefCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(0);
  }

  @Test
  void stateDefinitionExistsTestInValid() throws IOException {
    var model = "port def A { in attribute b: B; } part def C { port a: A; }";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new StateDefCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(1);
    assertThat(findings.get(0).getMsg()).contains("0xA70003");
  }

  @ParameterizedTest
  @ValueSource(strings = {"doubleInverter.sysml", "inverter.sysml", "port.sysml"})
  void testDoubleInverterModelValid(String inputValue) throws IOException {
    var modelPath = Paths.get(MODEL_PATH, "model", inputValue).toString();
    var ast = parser.parse(modelPath);
    assertThat(ast).isPresent();
    st.createSymbolTable(ast.get());
    st.runAdditionalCoCos(ast.get());
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new FlowCheckCoCo());
    checker.addCoCo(new PortDefinitionExistsCoCo());
    checker.addCoCo(new StateDefCoCo());
    checker.checkAll(ast.get());
    List<Finding> findings = getFindings();
    assertThat(findings).hasSize(0);
  }

  private List<Finding> getFindings() {
    Log.enableFailQuick(false);
    return Log.getFindings().stream().filter(f -> f.isWarning()).collect(Collectors.toList());
  }

}
