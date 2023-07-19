package symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
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

    var specification = component.getSpecification().get();

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
    var as = process("part def A; part def B refines A;");

    var refinedComponent = as.resolveMildComponent("B").get();

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

  @Disabled
  @Test
  public void testPortFullName() throws IOException {

    var as = process("port def A { attribute b: boolean; } part def B { port a: A; }");

    var comp = as.resolveMildComponent("B").get();

    var port = comp.getPorts().get(0);

    // Controversial: port "package" sollte von der port usage kommen nicht von der attribute usage.
    // Denn sonst koennen ports in unterschiedliche part defs denselben Id haben. Vielleicht ist das gewollt in CC da es keine usages existieren?
    // Dies hier hat auch Folgen fuer "port instances" resolution in Connectoren.
    assertThat(port.getFullName()).isEqualTo("B.a.b");

    // Und resolven wuerde auch nuetzlich sein aber bin nicht sicher ob das sinn macht
    var portFromResolution = as.resolvePort("B.a.b");
    assertThat(portFromResolution).isPresent();
  }

  @Disabled
  @Test
  public void testSubcomponents() throws IOException {
    var as = process(
        "port def In { " +
            "  in attribute val : boolean;" +
            "}" +
            "part def InstDef {" +
            "  port i: In;" +
            "}" +
            "part def Comp {" +
            "  port input: In;" +
            "  part inst1: InstDef;" +
            "}");

    var partUsages = as.resolvePartDef("Comp").get().getSpannedScope().getLocalPartUsageSymbols();

    var compComponent = as.resolveMildComponent("Comp").get();

    assertThat(compComponent.getSubcomponents()).hasSameSizeAs(partUsages);

    var subcompDef = as.resolveMildComponent("InstDef").get();

    var inst = compComponent.getSubcomponents().get(0);

    assertThat(inst.getType().getTypeInfo()).isEqualTo(subcompDef); // wahrscheinlich ist equals zu streng. fullName.equals koennte reichen?
  }

  @Disabled
  @Test
  public void testConnector() throws IOException {
    var as = process(
        "port def In { " +
            "  in attribute val : boolean;" +
            "}" +
            "part def InstDef {" +
            "  port i: In;" +
            "}" +
            "part def Comp {" +
            "  port input: In;" +
            "  part inst1: InstDef;" +
            "  connect input to inst1::i;" +
            "}");

    var connections = as.resolvePartDef("Comp").get().getAstNode().getSysMLElements(ASTConnectionUsage.class);

    var compositionComponent = as.resolveMildComponent("Comp").get();

    assertThat(compositionComponent.getConnectorsList()).hasSameSizeAs(connections);
  }
}
