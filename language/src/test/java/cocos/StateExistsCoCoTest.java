/* (c) https://github.com/MontiCore/monticore */
package cocos;


import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.StateExistsCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateExistsCoCoTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
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
    Optional<ASTSysMLModel> ast = parser.parse_String("state def v { state S; state T; transition first S then T; }");
    assertThat(ast).isPresent();

    st.createSymbolTable(ast.get());
    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new StateExistsCoCo());
    checker.checkAll(ast.get());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testInvalidState() throws IOException{
    Optional<ASTSysMLModel> ast = parser.parse_String("state def i { transition first S then T; }");
    assertThat(ast).isPresent();

    st.createSymbolTable(ast.get());
    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new StateExistsCoCo());
    checker.checkAll(ast.get());
    assertThat(Log.getFindings().stream().map(Finding::getMsg))
        .contains("0x10029 Source state is not defined", "0x10030 Target state is not defined");
  }
}

