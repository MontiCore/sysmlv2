/* (c) https://github.com/MontiCore/monticore */
package astrules;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ASTPartDefTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @Test
  public void test_getRefinements() throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "part def A; part def B refines A; part def C refines A, B;");
    assertThat(ast).isPresent();

    // Needs basic ST
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast.get());

    ASTPartDef A = (ASTPartDef) ast.get().getSysMLElement(0);
    ASTPartDef B = (ASTPartDef) ast.get().getSysMLElement(1);
    ASTPartDef C = (ASTPartDef) ast.get().getSysMLElement(2);

    // None
    assertThat(A.getRefinements()).isEmpty();

    // One
    assertThat(B.getRefinements()).hasSize(1);
    assertThat(B.getRefinements().get(0)).isEqualTo(A.getSymbol());

    // A list
    assertThat(C.getRefinements()).hasSize(2);
    assertThat(C.getRefinements().get(0)).isEqualTo(A.getSymbol());
    assertThat(C.getRefinements().get(1)).isEqualTo(B.getSymbol());
  }

}
