package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.TSynAcceptActionCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TSynAcceptActionCoCoTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
  }

  @BeforeEach
  public void clear() {
    Log.clearFindings();
  }

  @Test
  void tsynAutomaton_withoutAccept_mustNotLogError() {
    var tool = new SysMLv2Tool();
    tool.init();
    ASTSysMLModel ast = null;
    try {
      ast = SysMLv2Mill.parser().parse_String(valid).get();
    }
    catch (Exception e) {
      fail("Model was not parsable");
    }

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new TSynAcceptActionCoCo());
    checker.checkAll(ast);
    var errors = Log.getFindings().stream().collect(Collectors.toList());
    assertTrue(errors.isEmpty(),
        "Expected no errors, but got:\n" + errors);
  }

  @Test
  void tsynAutomaton_withAccept_mustLogError() {
    var tool = new SysMLv2Tool();
    tool.init();
    ASTSysMLModel ast = null;
    try {
      ast = SysMLv2Mill.parser().parse_String(invalid).get();
    }
    catch (Exception e) {
      fail("Model was not parsable");
    }

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new TSynAcceptActionCoCo());
    checker.checkAll(ast);
    assertFalse(Log.getFindings().isEmpty());
  }

  String valid = "part def TestWithoutAccept {\n"
      + "  port input: int;\n"
      + "  port output: ~int;\n"
      + "\n"
      + "  #tsyn exhibit state behavior {\n"
      + "    transition\n"
      + "      first S\n"
      + "      then S;\n"
      + "  }\n"
      + "}";

  String invalid = "part def TestWithAccept {\n"
      + "  port input: int;\n"
      + "\n"
      + "  #tsyn exhibit state behavior {\n"
      + "    transition ok\n"
      + "      first S\n"
      + "      then S;\n"
      + "\n"
      + "    transition bad\n"
      + "      first S\n"
      + "      accept input.val\n"
      + "      then S;\n"
      + "   }\n"
      + "}";

}
