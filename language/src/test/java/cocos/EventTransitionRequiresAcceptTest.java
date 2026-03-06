package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.EventTransitionRequiresAccept;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EventTransitionRequiresAcceptTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
  }

  @BeforeEach
  public void clear() {
    Log.clearFindings();
  }

  @Test
  void eventAutomaton_withAccept_mustNotLogError() {
    parseAndCheck(valid);
    var errors = Log.getFindings().stream()
        .filter(s -> s.isError())
        .collect(Collectors.toList());
    assertTrue(errors.isEmpty(),
        "Expected no errors, but got:\n" + errors);
  }

  @Test
  void eventAutomaton_withoutAccept_mustLogError() {
    parseAndCheck(invalid);
    assertFalse(Log.getFindings().isEmpty());
  }

  void parseAndCheck(String model) {
    var tool = new SysMLv2Tool();
    tool.init();
    ASTSysMLModel ast = null;
    try {
      ast = SysMLv2Mill.parser().parse_String(model).get();
    }
    catch (Exception e) {
      fail("Model was not parsable");
    }

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new EventTransitionRequiresAccept());
    checker.checkAll(ast);
  }

  String valid = "part def TestWithAccept {\n"
      + "  port input: Booleans;\n"
      + "  port output: ~Booleans;\n"
      + "\n"
      + "  exhibit state behavior {\n"
      + "    entry;\n"
      + "      then S;\n"
      + "\n"
      + "    state S;\n"
      + "\n"
      + "    transition\n"
      + "      first S\n"
      + "      accept input.val\n"
      + "      if input.val == true\n"
      + "      do action {\n"
      + "        send false to output.val;\n"
      + "      }\n"
      + "      then S;\n"
      + "  }\n"
      + "}";

  String invalid = "part def TestWithoutAccept {\n"
        + "  port input: Booleans;\n"
        + "\n"
        + "  exhibit state behavior {\n"
        + "    entry; then S;\n"
        + "\n"
        + "    state S;\n"
        + "\n"
        + "    transition ok\n"
        + "      first S\n"
        + "      accept input.val\n"
        + "      then S;\n"
        + "\n"
        + "    transition bad\n"
        + "      first S\n"
        + "      then S;\n"
        + "  }\n"
        + "}";

}
