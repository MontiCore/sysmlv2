package typecheck;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBooleanTC3;
import de.monticore.lang.sysmlv2.cocos.MaxOneDirectReturnInCalcDefCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class checks the implementation of SysML calc defs
 * as MontiCore function symbols. MontiCore establishes the
 * set of basic symbols as types, variables, and functions.
 * This is different from the plethora of SysML keywords that
 * are not meaningful for symbol resolution (and specifically
 * not relevant for type checking).
 */
public class CalcDefAsFunctionTest {
  @Test
  public void testValid() throws Exception {
    LogStub.init();
    Log.clearFindings();

    var model = "attribute def Foo { calc def bar { return : boolean; } } \n" +
        "attribute f: Foo; \n" +
        "constraint { f.bar() } \n";

    SysMLv2Tool tool = new SysMLv2Tool();
    tool.init();

    var parser = SysMLv2Mill.parser();
    var ast = parser.parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBooleanTC3());
    checker.addCoCo(new MaxOneDirectReturnInCalcDefCoCo());
    checker.checkAll(ast);

    assertTrue(Log.getFindings().isEmpty(), ()-> Log.getFindings().toString());
  }

  @Test
  void testInvalid() throws Exception {
    LogStub.init();
    Log.clearFindings();

    var model = "attribute def Foo { calc def bar { return : nat; } } \n" +
        "attribute f: Foo; \n" +
        "constraint { f.bar() } \n";

    SysMLv2Tool tool = new SysMLv2Tool();
    tool.init();

    var parser = SysMLv2Mill.parser();
    var ast = parser.parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBooleanTC3());
    checker.addCoCo(new MaxOneDirectReturnInCalcDefCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).hasSize(1);
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x80002 The expression type is 'nat' but should be boolean!");
  }
}
