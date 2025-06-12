package typecheck;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeCheck4StreamConstructorExpressionsTest {

  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void setup() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void clear() {
    Log.clearFindings();
    tool.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<true>} }",
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<false, true, true>} }"
  })
  public void test4validStream(String model) throws IOException {
    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var deriver = new SysMLDeriver();
    var type = deriver.deriveType(expr);
    assertTrue(type.isPresentResult());
    assertThat(type.getResult().printFullName()).isEqualTo("Stream<boolean>");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<true, 5>} }",
      "port def F { attribute a: boolean; } part s { port f: F; constraint e {<false, <true> >} }"
  })
  public void test4invalidStream(String model) throws IOException {
    var ast = parser.parse_String(model);

    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();

    tool.createSymbolTable(astSysMLModel);
    tool.completeSymbolTable(astSysMLModel);
    tool.finalizeSymbolTable(astSysMLModel);

    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPartUsage = sysmlelements.get(1);
    var constraintUsage = ((ASTPartUsage) astPartUsage).getSysMLElement(1);
    var expr = ((ASTConstraintUsage) constraintUsage).getExpression();
    var deriver = new SysMLDeriver();
    var type = deriver.deriveType(expr);
    assertTrue(type.isPresentResult());
    assertFalse(Log.getFindings().isEmpty());
    assertThat(type.getResult().printFullName()).isEqualTo("Stream<Obscure>");
  }
}
