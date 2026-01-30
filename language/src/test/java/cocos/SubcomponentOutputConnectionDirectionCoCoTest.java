/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.cocos.SubcomponentOutputConnectionDirectionCoCo;
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

public class SubcomponentOutputConnectionDirectionCoCoTest {

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
  public class OutputConnectionTests {

    @Test
    public void testValid() throws IOException {
      String validModel =
          "port def OutPort { out attribute data: int; }"
              + "port def InPort { in attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port input: InPort; }"
              + "part def C { port output: OutPort; }"
              + "part def System {"
              +   "port sysOutput: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "part c: C;"
              +   "connect a.output to b.input;"          // (Sub) Output -> (Sub) Input
              +   "connect c.output to sysOutput;"        // (Sub) Output -> (main) Output
              + "}";
      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testValidSwitchSrcAndTgt() throws IOException {
      String validModel =
          "port def OutPort { out attribute data: int; }"
              + "port def InPort { in attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port input: InPort; }"
              + "part def C { port output: OutPort; }"
              + "part def System {"
              +   "port sysOutput: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "part c: C;"
              +   "connect b.input to a.output;"          // (Sub) Output -> (Sub) Input
              +   "connect sysOutput to c.output;"        // (Sub) Output -> (main) Output
              + "}";
      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testValidConjugatedModel() throws IOException {
      String validModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port input: ~OutPort; }"
              + "part def C { port output: OutPort; }"
              + "part def System {"
              +   "port sysOutput: OutPort;"
              +   "part a: A;"
              +   "part b: B;"
              +   "part c: C;"
              +   "connect a.output to b.input;"          // (Sub) Output -> (Sub) Input
              +   "connect c.output to sysOutput;"        // (Sub) Output -> (main) Output
              + "}";
      var ast = parse(validModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(0);
    }

    @Test
    public void testInvalidSubOutToSubOut() throws IOException {
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def B { port output: OutPort; }"
              + "part def System {"
              +   "part a: A;"
              +   "part b: B;"
              +   "connect a.output to b.output;"         // (Sub) Output -> (Sub) Output
              + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA5");
    }

    @Test
    public void testInvalidSubOutToMainInConjugatedModel() throws IOException {
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def System {"
              +   "port sysInput: ~OutPort;"
              +   "part a: A;"
              +   "connect a.output to sysInput;"         // (Sub) Output -> (main) Input
              + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA5");
    }

    @Test
    public void testInvalidSubOutToMainInSwitchSrcAndTgt() throws IOException {
      /* (Sub) Output -> (main) Input
       * (also caught by ParentComponentInputConnectionDirectionCoCo so in the
       * actual Server one this connection would throw 0x10AA5 and 0x10AA6
       */
      String invalidModel =
          "port def OutPort { out attribute data: int; }"
              + "port def InPort { in attribute data: int; }"
              + "part def A { port output: OutPort; }"
              + "part def System {"
              +   "port sysInput: InPort;"
              +   "part a: A;"
              +   "connect sysInput to a.output;"
              + "}";

      var ast = parse(invalidModel);
      createSt(ast);
      var errors = check(ast);
      assertThat(errors).hasSize(1);
      assertThat(errors.get(0).getMsg()).contains("0x10AA5");
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
      checker.addCoCo(new SubcomponentOutputConnectionDirectionCoCo());
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
