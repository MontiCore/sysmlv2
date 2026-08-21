package cocos;

import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTEnumUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlparts.symboltable.adapters.EnumDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
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
  public void shouldNotReportErrorPort() throws IOException {
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
  public void shouldReportErrorPort() throws IOException {
    var as = process("port def P; attribute d: P;");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isNotEmpty();
  }


  @Test
  public void shouldNotReportErrorPart() throws IOException {
    var as = process("part def P; part d: P;");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPartUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    var p = as.resolvePartDef("P");
    assertTrue(p.isPresent());
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void shouldReportErrorPart() throws IOException {
    var as = process("part def P; attribute d: P;");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTPartUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isNotEmpty();
  }

  @Test
  public void shouldNotReportErrorEnum() throws IOException {
    var as = process("enum def P; enum d: P;");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTEnumUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    var p = as.resolveEnumDef("P");
    assertTrue(p.isPresent());
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void shouldReportErrorEnum() throws IOException {
    var as = process("port def P; attribute d: P;");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTEnumUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isNotEmpty();
  }

  @Test
  public void shouldNotReportErrorAttribute() throws IOException {// does not work for standard library attributes
    var as = process("attribute def P : String;  attribute a : String; ");
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTAttributeUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    var p = as.resolveAttributeDef("P");
    var p_stlib = as.resolveType("P").filter(type ->
        !(type instanceof PartDef2TypeSymbolAdapter)
            && !(type instanceof PortDef2TypeSymbolAdapter)
            && !(type instanceof EnumDef2TypeSymbolAdapter));

    assertTrue(p.isPresent() || p_stlib.isPresent());
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void shouldReportErrorAttribute() throws IOException {
    var as = process("part def P;  attribute a : P;");

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLPartsASTAttributeUsageCoCo) new DefsAndUsagesHaveTheSameTypeCoCo());
    Log.enableFailQuick(false);
    checker.checkAll((ASTSysMLv2Node) as.getAstNode());
    assertThat(Log.getFindings()).isNotEmpty();
  }
}
