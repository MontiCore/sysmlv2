/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.cocos.PartTypeDefinitionExistsCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PartTypeDefinitionExistsCoCoTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }

  @Test
  public void testValid() throws IOException {
    String validModel =
          "part def A;"
        + "part def B{"
        +   "part a: A;"
        + "}";

    var ast = parse(validModel);
    createSt(ast);
    var errors = check(ast);
    assertThat(errors).hasSize(0);
  }

  @Test
  public void testInvalid() throws IOException {
    String invalidModel =
          "item def A;"
        + "part def B {"
        +   "part a: A;"
        + "}";

    var ast = parse(invalidModel);
    createSt(ast);
    var errors = check(ast);
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("0x10AA1");
  }

  @Test
  public void testWithImports_Valid() throws IOException {
    String parent = "package P { part def MyPartDef; }";
    String model = "import P::*; part myPart : MyPartDef;";

    var parentAst = parse(parent);
    createSt(parentAst);

    var ast = parse(model);
    createSt(ast);

    var errors = check(ast);
    assertThat(errors).hasSize(0);
  }

  @Test
  public void testWithImports_Invalid() throws IOException {
    String parent = "package P { item def MyPartDef; }";
    String model = "import P::*; part myPart : MyPartDef;";

    var parentAst = parse(parent);
    createSt(parentAst);

    var ast = parse(model);
    createSt(ast);

    var errors = check(ast);
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("0x10AA1");
  }

  // TODO Inlinen
  private ASTSysMLModel parse(String model) throws IOException {
    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    return optAst.get();
  }

  // TODO Inlinen
  private ISysMLv2ArtifactScope createSt(ASTSysMLModel ast) {
    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    return scope;
  }

  // TODO Inlinen
  private List<Finding> check(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new PartTypeDefinitionExistsCoCo());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    return Log.getFindings().stream().filter(Finding::isError).collect(
        Collectors.toList());
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
