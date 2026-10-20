/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.SpecializationExistsTC3;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.se_rwth.commons.logging.Log;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prüft das Finden von Types der Specializations
 */
public class SpecializationExistsCoCoTest {

  @Test
  public void testMissingDefinition() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    Log.clearFindings();

    var model = "requirement Invalid { subject s: NonExistent; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    Log.clearFindings();

    var model = "part def Existent; requirement Valid { subject s: Existent; }";


    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);


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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    Log.clearFindings();


    var model = "state def Existent; requirement Valid { subject s: Existent; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    Log.clearFindings();


    var model = "attribute def Existent; part def Valid { attribute e: List<Existent>; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();
    Log.clearFindings();

    var model = "part p: P { part def P; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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

}
