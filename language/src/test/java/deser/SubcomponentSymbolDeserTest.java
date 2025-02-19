package deser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Demonstriert, dass SysML-Symbole als CompSymbol serialisiert werden können
 */
public class SubcomponentSymbolDeserTest extends NervigeSymboltableTests {

  @Test
  public void testSubcomponent() throws IOException {
    var as = process("part def B; part def A { part b: B; }");
    setupComponentConnectorSerialization();

    var comp = as.resolveComponent("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{"
            + "\"generated-using\":\"www.MontiCore.de technology\","
            + "\"name\":\"A\","
            + "\"symbols\":["
            + "{"
            + "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\","
            + "\"name\":\"A\","
            + "\"fullName\":\"A\","
            + "\"spannedScope\":{"
            + "\"symbols\":["
            + "{"
            + "\"kind\":\"de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol\","
            + "\"name\":\"b\","
            + "\"fullName\":\"A.b\","
            + "\"types\":["
            + "{"
            + "\"kind\":\"de.monticore.types.check.SymTypeOfObject\","
            + "\"objName\":\"B\""
            + "}"
            + "]"
            + "}"
            + "]"
            + "}"
            + "}"
            + "]"
            + "}");
  }

}
