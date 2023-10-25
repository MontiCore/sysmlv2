package symboltable;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomatonSymbolAdapterTest extends NervigeSymboltableTests {

  @Test
  public void testSCState() throws IOException {
    var as = process("state S;");

    var state = as.resolveSCState("S");
    assertThat(state).isPresent();
    assertThat(state.get().getName()).isEqualTo("S");
  }

}
