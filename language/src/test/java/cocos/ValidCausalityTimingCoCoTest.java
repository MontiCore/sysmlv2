/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.ValidCausalityTimingCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidCausalityTimingCoCoTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
  }

  @Test
  void testDelayedTimingAndMetadataNameCanCoexist() throws IOException {
    var ast = parser.parse_String("package Demo { metadata def delayed; part def P { timing delayed; } }");

    assertThat(ast).isPresent();
    assertThat(parser.hasErrors()).isFalse();

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ValidCausalityTimingCoCo());
    checker.checkAll(ast.get());

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void testInvalidTimingValueProducesFinding() throws IOException {
    var ast = parser.parse_String("part def P { timing asap; }");

    assertThat(ast).isPresent();
    assertThat(parser.hasErrors()).isFalse();

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ValidCausalityTimingCoCo());
    checker.checkAll(ast.get());

    assertThat(Log.getFindings()).isNotEmpty();
    assertThat(Log.getFindings().get(0).getMsg()).contains("0x10AA8");
  }
}