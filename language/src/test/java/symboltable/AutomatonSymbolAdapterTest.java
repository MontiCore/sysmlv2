package symboltable;

import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.lang.componentconnector._ast.ASTConfiguration;
import de.monticore.lang.componentconnector._ast.ASTOutput;
import de.monticore.lang.componentconnector._ast.ASTStateSpace;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.sysmlv2._prettyprint.SysMLv2FullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
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
    assertThat(component.get()).isInstanceOf(MildComponentSymbol.class);

    assertTrue(((MildComponentSymbol)component.get()).isStateBased());

    var aut = ((MildComponentSymbol)component.get()).getAutomaton();
    assertThat(aut).isNotNull();
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

  @Test
  public void testInitialConfig() throws IOException {
    var as = process("port def B {\n"
        + "  in attribute val: boolean;\n"
        + "}\n"
        + "\n"
        + "part def A {\n"
        + "  port i: B;\n"
        + "  port o: ~B;\n"
        + "\n"
        + "  exhibit state aut {\n"
        + "    entry action {\n"
        + "      // Do one with parameter as entry action\n"
        + "      send 4 to o.val;\n"
        + "    }\n"
        + "      then S;\n"
        + "\n"
        + "  }\n"
        + "}");

    var aut = as.resolveAutomaton("A").get();

    var initialConfig = aut.getInitialConfiguration(0);

    var output = initialConfig.getOutput(0);

    var port = output.getPortSymbol();
    var state = output.getValue();

    assertThat(port.getName()).isEqualTo("o.val");
    assertThat(new SysMLv2FullPrettyPrinter(new IndentPrinter()).prettyprint(state)).isEqualTo("4");
  }

  @Test
  public void testEventTransition() throws IOException {
    var as = process("port def B {\n"
        + "  in attribute val: boolean;\n"
        + "}\n"
        + "\n"
        + "part def A {\n"
        + "  port i: B;\n"
        + "  port o: ~B;\n"
        + "\n"
        + "  exhibit state aut {\n"
        + "    state S;\n"
        + "    transition t\n"
        + "      first S\n"
        + "      accept i.val\n"
        + "      if true\n"
        + "      do action { send !i.val to o.val; }\n"
        + "      then S;\n"
        + "  }\n"
        + "}");

    var aut = as.resolveAutomaton("A").get();

    assertThat(aut.getTickTransitionsList()).isEmpty();

    var evTransitions = aut.getEventTransitionsList();
    assertThat(evTransitions).isNotEmpty();

    var trans = evTransitions.get(0);
    assertThat(trans.getPortSymbol().getName()).isEqualTo("i.val");

    ASTConfiguration result = trans.getTransition(0).getResult();
    assertThat(result.getState().getName()).isEqualTo("S");

    ASTOutput output = result.getOutput(0);
    assertThat(output.getPortSymbol().getName()).isEqualTo("o.val");
    assertThat(output.getValue()).isInstanceOf(ASTLogicalNotExpression.class);
  }
}
