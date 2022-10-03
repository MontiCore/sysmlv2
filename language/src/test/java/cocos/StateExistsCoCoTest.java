package cocos;


import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.StateExistsCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateExistsCoCoTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  private final String validPath = "src/test/resources/cocos/StateExists/0_valid.sysml";
  private final String invalidPath = "src/test/resources/cocos/StateExists/0_invalid.sysml";

  @BeforeAll
  public static void init(){
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void disableFailQuick() {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  @Test
  public void testValidState() throws IOException{
    Optional<ASTSysMLModel> ast = parser.parse(validPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new StateExistsCoCo());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  public void testInvalidState() throws IOException{
    Optional<ASTSysMLModel> ast = parser.parse(invalidPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo(new StateExistsCoCo());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }
}

