package astrules;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ASTPortUsageTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @Test
  public void test_getCardinality() throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String("port a:A[3];");
    assertThat(ast).isPresent();

    var cardinility = ((ASTPortUsage)ast.get().getSysMLElement(0)).getCardinality();
    assertThat(cardinility).isPresent();
    assertThat(cardinility.get().getLowerBound()).isEqualTo(cardinility.get().getUpperBound()).isEqualTo(3);
  }

}
