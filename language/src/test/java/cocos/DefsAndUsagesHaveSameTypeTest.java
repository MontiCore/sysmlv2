package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlv2.cocos.DefsAndUsagesHaveTheSameTypeCoCo;
import de.monticore.lang.sysmlv2._ast.ASTSysMLv2Node;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefsAndUsagesHaveSameTypeTest extends NervigeSymboltableTests{

  @BeforeEach
  public void clear() {
    LogStub.init();
  }

  @Test
  public void testValid() throws IOException {
    var as = process("port def P; port d: P;");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    var p = as.resolvePortDef("P");
    assertTrue(p.isPresent());
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testInvalid() throws IOException {
    var as = process("attribute def P; attribute d: P;");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isNotEmpty();
  }
}
