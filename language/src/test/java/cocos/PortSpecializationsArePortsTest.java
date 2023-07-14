package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlparts.coco.PortSpecializationsArePorts;
import de.monticore.lang.sysmlv2._ast.ASTSysMLv2Node;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für {@link PortSpecializationsArePorts}
 */
public class PortSpecializationsArePortsTest extends NervigeSymboltableTests {

  @BeforeEach
  public void clear() {
    LogStub.init();
  }

  @Test
  public void testValidPortUsage() throws IOException {
    var as = process("port a: A; port def A; ");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new PortSpecializationsArePorts());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testInvalidPortUsage() throws IOException {
    var as = process("port a: A; part def A; ");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new PortSpecializationsArePorts());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).hasSize(1).first().matches(f -> f.getMsg().startsWith("0xMPf001"));
  }

}
