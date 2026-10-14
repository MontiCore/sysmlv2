/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.cocos.SpecializationExistsTC3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
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

/**
 * Prüft das Finden von Types der Specializations
 */
public class SpecializationExistsCoCoTest {

  @Test
  public void testMissingDefinition() throws IOException {
    SysMLv2Mill.init();

    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();

    var model = "requirement Invalid { subject s: NonExistent; }";

    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    var ast = optAst.get();

    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());

    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("0xA0324 Cannot find symbol NonExistent");

    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  @Test
  public void testExistingDefinition() throws IOException {
    SysMLv2Mill.init();

    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();

    var model = "part def Existent; requirement Valid { subject s: Existent; }";


    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    var ast = optAst.get();

    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());

    assertThat(errors).hasSize(0);

    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  // Should also work with state defs
  @Test
  public void testExistingStateDefinition() throws IOException {
    SysMLv2Mill.init();

    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();

    var model = "state def Existent; requirement Valid { subject s: Existent; }";

    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    var ast = optAst.get();

    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());

    assertThat(errors).hasSize(0);

    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  @Test
  public void testExistingCollectionType() throws IOException {
    SysMLv2Mill.init();

    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();

    var model = "attribute def Existent; part def Valid { attribute e: List<Existent>; }";

    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    var ast = optAst.get();

    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());

    assertThat(errors).hasSize(0);

    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  @Disabled("Die CoCo sucht 'P' im enclosing Scope von ':P', was dem der Part "
      + "Usage entspricht.")
  @Test
  public void testInvalid() throws IOException {
    SysMLv2Mill.init();

    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();

    var model = "part p: P { part def P; }";

    var optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    var ast = optAst.get();

    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());

    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("0xA0324");

    Log.clearFindings();
    Log.enableFailQuick(true);
  }

  /*
  @BeforeAll
  static void setup() {
    SysMLv2Mill.init();
  }

  @BeforeEach
  void clear() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLTypeCheck3.reset();
    SysMLTypeCheck3.init();
    Log.clearFindings();
  }





  private ISysMLv2ArtifactScope createSt(ASTSysMLModel ast) {
    var tool = new SysMLv2Tool();
    var scope = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    return scope;
  }

  private List<Finding> check(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SpecializationExistsTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    return Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
  */

}
