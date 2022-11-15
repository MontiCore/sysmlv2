package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// TODO Gehört nach ConstraintTest
public class RequirementSubjectTest {

  @BeforeAll
  static void setup() {
    SysMLv2Mill.init();
  }

  @BeforeEach
  void clear() {
    SysMLv2Mill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    SysMLv2Mill.addStreamType();
  }

  @Disabled
  @Test
  public void testValid() throws IOException {
    var model = "part def S { attribute a: boolean; } requirement Tester { subject s: S; constraint t { a } }";
    var ast = parse(model);
    createSt(ast);
    var errors = check(ast);
    assertThat(errors).hasSize(0);
  }

  @Disabled
  @Test
  public void testInvalid() throws IOException {
    var model = "part def S { attribute a: int; } requirement Tester { subject s: S; constraint t { a } }";
    var ast = parse(model);
    createSt(ast);
    var errors = check(ast);
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("should be boolean");
  }

  private ASTSysMLModel parse(String model) throws IOException {
    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    return optAst.get();
  }

  private ISysMLv2ArtifactScope createSt(ASTSysMLModel ast) {
    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    return scope;
  }

  private List<Finding> check(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBoolean());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    return Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }

}
