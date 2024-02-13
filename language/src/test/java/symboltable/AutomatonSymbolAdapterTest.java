package symboltable;

import de.monticore.lang.automaton._ast.ASTStateSpace;
import de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbol;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomatonSymbolAdapterTest extends NervigeSymboltableTests {

  @Test
  public void testAutomaton() throws IOException {
    var as = process("part def A { exhibit state S; }");

    var component = as.resolveComponent("A");
    assertThat(component).isPresent();
    assertThat(component.get()).isInstanceOf(ExtendedMildComponentSymbol.class);

    assertTrue(((ExtendedMildComponentSymbol)component.get()).isStateBased());
    var optAut = ((ExtendedMildComponentSymbol)component.get()).getAutomaton();
    assertThat(optAut).isPresent();

    var aut = optAut.get();
    assertThat(aut.getName()).isEqualTo("A");

    var autFromResolve = as.resolveAutomaton("A");
    assertThat(autFromResolve).isPresent();
  }

  @Test
  public void testInvalidAutomaton() throws IOException {
    var as = process("part def A { state S; }");

    assertThat(as.resolveAutomaton("A")).isNotPresent();
    assertThat(as.resolveAutomaton("S")).isNotPresent();
  }

  @Test
  public void testVariables() throws IOException {
    var as = process("part def A { exhibit state S { attribute a: boolean; } }");

    ASTStateSpace stateSpace = as.resolveAutomaton("A").get().getStateSpace();

    var variables = stateSpace.getVariablesSymbolList();
    assertThat(variables).isNotEmpty();
    var optVariable = variables.get(0);
    assertThat(optVariable).isPresent();

    var variable = optVariable.get();
    assertThat(variable.getName()).isEqualTo("a");
    assertThat(variable.getFullName()).isEqualTo("A.S.a");
  }

  @Test
  public void testStates() throws IOException {
    var as = process("part def A { exhibit state S { state S1; } }");

    ASTStateSpace stateSpace = as.resolveAutomaton("A").get().getStateSpace();

    var states = stateSpace.getStatesList();
    assertThat(states).isNotEmpty();

    var state = states.get(0);
    assertThat(state.getBaseName()).isEqualTo("S1");
    assertThat(state.getQName()).isEqualTo("A.S.S1");
  }
}
