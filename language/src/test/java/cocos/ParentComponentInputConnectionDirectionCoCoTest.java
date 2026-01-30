/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.cocos.ParentComponentInputConnectionDirectionCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ParentComponentInputConnectionDirectionCoCoTest {

  private static final String MODEL_PATH = "src/test/resources/parser";

  private SysMLv2Parser parser = SysMLv2Mill.parser();

  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }

  @Nested
  public class InputConnectionTests {
    @Test
    public void testValid() throws IOException {
      String validModel =
          "port def InPort { in attribute data: int; }"
        + "port def OutPort { out attribute data: int; }"
        + "part def A { port input: InPort; }"
        + "part def B { port output: OutPort; }"
        + "part def System {"
        +   "port sysIn: InPort;"
        +   "port sysInAnother: InPort;"
        +   "port sysOut: OutPort;"
        +   "part a: A;"
        +   "part b: B;"
        +   "connect sysIn to a.input;"
        +   "connect sysInAnother to sysOut;"
        + "}";

      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testValidSwitchTgtAndSrc() throws IOException {
      String validModel =
          "port def InPort { in attribute data: int; }"
              + "port def OutPort { out attribute data: int; }"
              + "part def A { port input: InPort; }"
              + "part def B { port output: OutPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "port sysInAnother: InPort;"
              +   "port sysOut: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "connect a.input to sysIn;"
              +   "connect sysOut to sysInAnother;"
              + "}";

      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testValidConjugatedModel() throws IOException {
      String validModel =
          "port def InPort { in attribute data: int; }"
              + "part def A { port input: InPort; }"
              + "part def B { port output: ~InPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "port sysInAnother: InPort;"
              +   "port sysOut: ~InPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "connect sysIn to a.input;"
              +   "connect sysInAnother to sysOut;"
              + "}";

      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testInvalid() throws IOException {
      String invalidModel =
          "port def InPort { in attribute data: int; }"
        + "port def OutPort { out attribute data: int; }"
        + "part def A { port output: OutPort; }"
        + "part def System {"
        +   "port sysIn: InPort;"
        +   "part a: A;"
        +   "connect sysIn to a.output;"
        + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA6");
    }

    @Test
    public void testInvalidConjugatedModel() throws IOException {
      String invalidModel =
          "port def InPort { in attribute data: int; }"
              + "part def A { port output: ~InPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "part a: A;"
              +   "connect sysIn to a.output;"
              + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA6");
    }

    @Test
    public void testInvalidConjugatedModelSwitchTgtAndSrc() throws IOException {
      /* (Sub) Output -> (main) Input
       * (also caught by SubcomponentOutputConnectionDirectionCoCo so in the
       * actual Server one this connection would throw 0x10AA5 and 0x10AA6
       */
      String invalidModel =
          "port def InPort { in attribute data: int; }"
              + "part def A { port output: ~InPort; }"
              + "part def System {"
              +   "port sysIn: InPort;"
              +   "part a: A;"
              +   "connect a.output to sysIn;"
              + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA6");
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
      checker.addCoCo(new ParentComponentInputConnectionDirectionCoCo());
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
}
