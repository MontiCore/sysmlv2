package typecheck;

import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.symboltable.completers.TypesCompleter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfTuple;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TupleTypeCompleterTest {

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

  // @Disabled
  @Test
  public void testTuples() throws IOException {
    var model = "private import ScalarValues::Boolean;\n"
        + "\n"
        + "port def Tuples {\n"
        + "  out attribute val: Tuple {\n"
        + "    redefines fst: Boolean;\n"
        + "    redefines snd: Boolean;\n"
        + "  }\n"
        + "}";
    var ast = parser.parse_String(model);
    assertThat(ast).isPresent();
    assertThat(Log.getFindings()).isEmpty();
    var astSysMLModel = ast.get();
    tool.createSymbolTable(astSysMLModel);
    var sysmlelements = astSysMLModel.getSysMLElementList();
    var astPortDef = sysmlelements.get(1);
    var astAttributeUsage = ((ASTPortDef) astPortDef).getSysMLElement(0);
    var node = (ASTAttributeUsage) astAttributeUsage;

    List<SymTypeExpression> result = TypesCompleter.getTypeCompletion(
        node.getSpecializationList(), false, node.getSysMLElementList());

    assertEquals(1, result.size());
    SymTypeExpression resultType = result.get(0);
    assertTrue(resultType.isTupleType());
    SymTypeOfTuple tupleType = resultType.asTupleType();
    assertEquals(2, tupleType.sizeTypes());
    assertTrue(tupleType.getType(0).printFullName().contains("Boolean"));
    assertTrue(tupleType.getType(1).printFullName().contains("Boolean"));

  }
}
