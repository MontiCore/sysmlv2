package symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
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

  @Test
  public void testPorts() throws IOException {
    var as = process("part def A { port i: Booleans; } port def Booleans { in attribute c: boolean; }");

    var inputs = as.resolveMildComponent("A").get().getIncomingPorts();
    assertThat(inputs).hasSize(1);
    assertThat(inputs.get(0).getType().printFullName()).isEqualTo("boolean");
  }

  @Test
  public void testFullName() throws IOException {
    var as = process("package P { part def A; }");
    var comp = as.resolveMildComponent("P.A");

    assertThat(comp).isPresent();
    assertThat(comp.get().getFullName()).isEqualTo("P.A");
  }

  /** Short version using a single constraint */
  @Test
  public void testSpecificationAbbreviated() throws IOException {
    var as = process("part def A { assert constraint B { true } }");

    var component = as.resolveMildComponent("A").get();
    var spec = component.getSpannedScope().resolveMildSpecification("B");
    assertThat(spec).isPresent();

    assertThat(component.getSpecification()).isPresent();
    assertThat(component.getSpecification()).isEqualTo(spec);
  }

  /** Long version using (pot. multiple) grouped constraints in a requirement */
  @Test
  public void testSpecification() throws IOException {
    var as = process("part def A { satisfy requirement B { assert constraint C { true } } }");

    var component = as.resolveMildComponent("A").get();
    var spec = component.getSpannedScope().resolveMildSpecification("B");
    assertThat(spec).isPresent();

    assertThat(component.getSpecification()).isPresent();
    assertThat(component.getSpecification()).isEqualTo(spec);
  }

  @Test
  public void testRefinementStart() throws IOException {
    var as = process("part def A; part def B refines A;");

    var refinedComponent = as.resolveMildComponent("A").get();

    assertThat(refinedComponent.getRefinementStart()).isPresent();
    assertThat(refinedComponent.getRefinementStart().get().getFullName()).isEqualTo("A");
  }

  @Test
  public void testUserStruct() throws IOException {
    var as = process("attribute def A { attribute b: boolean; }");

    var type = as.resolveType("A");
    assertThat(type).isPresent();
    assertThat(type.get()).isInstanceOf(OOTypeSymbol.class);

    var fields = ((OOTypeSymbol)type.get()).getFieldList();
    assertThat(fields).hasSize(1);

    var field = fields.stream().findFirst().get();
    assertThat(field.getName()).isEqualTo("b");
    assertThat(field.getType().printFullName()).isEqualTo("boolean");
  }

  @Test
  public void testUserEnum() throws IOException {
    var as = process("enum def A { enum B; }");

    var type = as.resolveType("A");
    assertThat(type).isPresent();
    assertThat(type.get()).isInstanceOf(OOTypeSymbol.class);

    var fields = ((OOTypeSymbol)type.get()).getFieldList();
    assertThat(fields).hasSize(1);

    var field = fields.stream().findFirst().get();
    assertThat(field.getName()).isEqualTo("B");
    assertThat(field.getType().printFullName()).isEqualTo("A");
  }
}
