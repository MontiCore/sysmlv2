package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import de.monticore.lang.sysmlactions._ast.ASTIfActionUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTPerformActionUsage;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IfElseActionsArrayListTest {

  SysMLv2Parser parser = SysMLv2Mill.parser();
  @BeforeAll
  public static void init() {
    Log.init();
    SysMLv2Mill.init();
  }

  @Test
  public void parsesIfandElseActionsSeparately() throws IOException {

    Optional<ASTSysMLModel> rootOpt = parser.parse("src/test/resources/parser/ifelseactions.sysml");

    assertTrue(rootOpt.isPresent(), "Expected model to parse");
    ASTSysMLModel root = rootOpt.get();

    ASTIfActionUsage dep = root.getSysMLElementList().stream()
        .filter(e -> e instanceof ASTIfActionUsage)
        .map(e -> (ASTIfActionUsage) e)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Expected an ASTIfActionUsage in the parsed model"));


    assertEquals(1, dep.sizeIfElems());
    assertTrue(dep.getIfElems(0) instanceof ASTPerformActionUsage);
    assertEquals("soundAlarm", ((ASTPerformActionUsage) dep.getIfElems(0)).getName());//should be a valid test without this line as well

    assertEquals(2, dep.sizeElseElems());
    assertTrue(dep.getElseElems(0) instanceof ASTActionUsage);
    assertTrue(dep.getElseElems(1) instanceof ASTActionUsage);
    assertEquals("sendNotification", ((ASTActionUsage) dep.getElseElems(0)).getName());//should be a valid test without this line as well
    assertEquals("beginMonitoring", ((ASTActionUsage) dep.getElseElems(1)).getName());//should be a valid test without this line as well
  }

}
