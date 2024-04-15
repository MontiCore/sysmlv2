package deser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Demonstriert, dass SysML-Symbole als CompSymbol serialisiert werden können
 */
public class PortSymbolDeserTest extends NervigeSymboltableTests {

  @Test
  public void testPorts() throws IOException {
    var as = process(
        "part def A { port b: B; } port def B { in attribute b: boolean; }");
    setupComponentConnectorSerialization();

    var comp = as.resolveComponent("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentSymbol\",\"name\":\"A\",\"ports\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".PortSymbol\",\"name\":\"b.b\",\"type\":"
            + "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\","
            + "\"primitiveName\":\"boolean\"},\"incoming\":true,"
            + "\"timing\":\"timed\",\"stronglyCausal\":true}]}]}");
  }

  @Disabled
  @Test
  public void testConjugatedPorts() throws IOException {
    var as = process(
        "part def A { port b: ~B; } port def B { in attribute b: boolean; }");
    setupComponentConnectorSerialization();

    var comp = as.resolveComponent("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentSymbol\",\"name\":\"A\",\"ports\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".PortSymbol\",\"name\":\"b.b\",\"type\":"
            + "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\","
            + "\"primitiveName\":\"boolean\"},\"incoming\":false,"
            + "\"timing\":\"timed\",\"stronglyCausal\":true}]}]}");
  }
}
