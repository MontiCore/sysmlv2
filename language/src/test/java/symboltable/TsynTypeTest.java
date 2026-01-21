package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TsynTypeTest {

  @BeforeAll
  static void setup() {
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void init() {
    LogStub.init();
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    SysMLv2Mill.addTsynTypes();
    Log.clearFindings();
  }

  @Test
  public void testEpsInModel() throws IOException {
    var tool = new SysMLv2Tool();

    var model = "part def Valid { attribute e: Eps; }";
    var optAst = SysMLv2Mill.parser().parse_String(model);

    assertThat(optAst).isPresent();
    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

}