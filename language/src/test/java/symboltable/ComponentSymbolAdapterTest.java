package symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
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

    var parameters = as.resolveMildComponent("A").get().getParameterList();
    assertThat(parameters).hasSize(1);
    assertThat(parameters.get(0).getType().printFullName()).isEqualTo("int");
  }

  @Test
  public void testPorts() throws IOException {
    var as = process("part def A { port i: Booleans; } port def Booleans { in attribute c: boolean; }");

    var inputs = as.resolveMildComponent("A").get().getIncomingPorts();
    assertThat(inputs).hasSize(1);
    assertThat(inputs.get(0).getType().printFullName()).isEqualTo("boolean");
    as.resolvePort("A.i.c");
  }

  @Test
  public void testConjugatedPorts() throws IOException {
    var as = process(""
        + "port def P { in attribute a: int; }"
        + "part def A { port o: ~P; }");

    var A = as.resolveMildComponent("A").get();
    assertThat(A.getIncomingPorts()).hasSize(0);
    assertThat(A.getOutgoingPorts()).hasSize(1);

    var o = A.getOutgoingPorts().get(0);
    assertThat(o.isStronglyCausal()).isTrue(); // Default
    assertThat(o.getTiming()).isEqualTo(Timing.TIMED);
    assertThat(o.isTypePresent()).isTrue();
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

    assertThat(component.isHistoryBased());
    assertThat(component.getSpecification()).isEqualTo(spec.get());
  }

  /** Long version using (pot. multiple) grouped constraints in a requirement */
  @Test
  public void testSpecification() throws IOException {
    var as = process("part def A { satisfy requirement B { assert constraint C { true } } }");

    var component = as.resolveMildComponent("A").get();
    var spec = component.getSpannedScope().resolveMildSpecification("B");
    assertThat(spec).isPresent();

    assertThat(component.isHistoryBased());
    assertThat(component.getSpecification()).isEqualTo(spec.get());

    var specification = component.getSpecification();

    // Scope ist nötigt um die Expressions (predicates) zu verstehen
    assertThat(specification.getEnclosingScope()).isNotNull();

    assertThat(specification.getPredicatesList()).hasSize(1);
    assertThat(specification.getPredicates(0)).isInstanceOf(ASTLiteralExpression.class);
    assertThat(((ASTLiteralExpression)specification.getPredicates(0)).getLiteral())
        .isInstanceOf(ASTBooleanLiteral.class);
    assertThat(((ASTBooleanLiteral)((ASTLiteralExpression)specification.getPredicates(0)).getLiteral())
        .getValue()).isTrue();
  }

  @Test
  public void testRefinement() throws IOException {
    var as = process("part def A; part def B refines A;");

    var refinedComponent = as.resolveMildComponent("B").get();

    assertThat(refinedComponent.getRefinementsList()).hasSize(1);
    assertThat(refinedComponent.getRefinements(0).printFullName()).isEqualTo("A");
  }

  @Test
  public void testRefinementStart() throws IOException {
    var as = process("part def A; part def B refines A; part def C refines A, B;");

    var refinedComponent = as.resolveMildComponent("C").get();

    // probably needs an equals implementation
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

    var typeOpt = as.resolveType("A");
    assertThat(typeOpt).isPresent();
    assertThat(typeOpt.get()).isInstanceOf(OOTypeSymbol.class);

    var type = (OOTypeSymbol) typeOpt.get();
    assertThat(type.isIsEnum()).isTrue();

    var fields = type.getFieldList();
    assertThat(fields).hasSize(1);

    var field = fields.stream().findFirst().get();
    assertThat(field.getName()).isEqualTo("B");
    assertThat(field.getType().printFullName()).isEqualTo("A");
  }

  @Test
  public void testPortFullName() throws IOException {
    var as = process("port def A { attribute b: boolean; } part def B { port a: A; }");
    var port = as.resolveMildComponent("B").get().getPorts().get(0);

    // Name muss widerspiegeln, dass der Port in B liegt und "a.b" heisst
    assertThat(port.getFullName()).isEqualTo("B.a.b");

    // Resolven in der PartDef muss wegen Komposition bzw den Konnektoren möglich sein
    var scope = as.resolveMildComponent("B").get().getSpannedScope();
    var resolved = scope.resolveMildPort("a.b");
    assertThat(resolved).isPresent();
    assertThat(resolved.get()).isEqualTo(port);
  }

  @Test
  public void testSubcomponents() throws IOException {
    var as = process("part def B; part def A { part b: B; }");

    var A = as.resolveMildComponent("A").get();
    assertThat(A.getSubcomponents()).hasSize(1);

    var b = A.getSubcomponents().get(0);
    assertThat(b.getFullName()).isEqualTo("A.b");
    assertThat(b.getType().printFullName()).isEqualTo("B");
    }

  @Test
  public void testConnector() throws IOException {
    var as = process(""
        + "port def P { attribute x: boolean; }"
        + "part def B { port p: P; }"
        + "part def A { port p: P; part b: B; connect p to b::p; }");

    var A = as.resolveMildComponent("A").get();
    assertThat(A.getConnectorsList()).hasSize(1);

    var c = A.getConnectors(0);
    assertThat(c.getSource().getQName()).isEqualTo("p.x");
    assertThat(c.getTarget().getQName()).isEqualTo("b.p.x");
  }

  /** Multiple attributes per Port */
  @Test
  public void testConnector2() throws IOException {
    var as = process(""
        + "port def P { attribute x: int; attribute y: int; }"
        + "part def B { port p: P; }"
        + "part def A { port p: P; part b: B; connect p to b::p; }");

    var A = as.resolveMildComponent("A").get();
    assertThat(A.getConnectorsList()).hasSize(2);

    var c = A.getConnectors(0);
    assertThat(c.getSource().getQName()).isEqualTo("p.x");
    assertThat(c.getTarget().getQName()).isEqualTo("b.p.x");
    c = A.getConnectors(1);
    assertThat(c.getSource().getQName()).isEqualTo("p.y");
    assertThat(c.getTarget().getQName()).isEqualTo("b.p.y");
  }

  @Test
  public void testParameterValues() throws IOException {
    var as = process(""
        + "part def B { final attribute v: nat; }"
        + "part def A { part b: B { final attribute v = 2; } }");

    var A = as.resolveMildComponent("A").get();
    var b = (MildInstanceSymbol)A.getSubcomponents("b").get();

    assertThat(b.sizeParameterValues()).isEqualTo(1);

    var value = b.getParameterValues(0);
    assertThat(value.getVariableSymbol().getFullName()).isEqualTo("B.v");
    assertThat(value.getValue()).isInstanceOf(ASTLiteralExpression.class);
    assertThat(((ASTLiteralExpression)value.getValue()).getLiteral()).isInstanceOf(ASTNatLiteral.class);
    assertThat(((ASTNatLiteral)((ASTLiteralExpression)value.getValue()).getLiteral()).getValue()).isEqualTo(2);
  }
}
