package symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testet die Adaptation von SysML <-> ComponentConnector
 */
public class ComponentSymbolAdapterTest extends NervigeSymboltableTests {

  @Test
  public void testMildComponent() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponent("A");
    assertThat(comp).isPresent();

    var mcomp = as.resolveMildComponent("A");
    assertThat(mcomp).isPresent();
  }

  @Test
  public void testParameters() throws IOException {
    var as = process("part def A { final attribute p: int; }");

    var parameters = as.resolveMildComponent("A").get().getParameters();
    assertThat(parameters).hasSize(1);
    assertThat(parameters.get(0).getType().printFullName()).isEqualTo("int");
  }

  @Disabled
  @Test
  public void testPorts() throws IOException {
    var as = process("part def A { port i: Booleans; } port def Booleans { in attribute c: boolean; }");

    var inputs = as.resolveMildComponent("A").get().getIncomingPorts();
    assertThat(inputs).hasSize(1);
    assertThat(inputs.get(0).getType().printFullName()).isEqualTo("boolean");
  }

}
