package deser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Testet die De-Serialisierung von Parametern einer Komponente
 */
public class ParameterDeserTest extends NervigeSymboltableTests {

  @Test
  public void testParameter() throws IOException {
    var as = process("part def A { final attribute p: int; }");
    setupComponentConnectorSerialization();

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{"
            + "\"generated-using\":\"www.MontiCore.de technology\","
            + "\"name\":\"A\","
            + "\"symbols\":["
            + "{"
            + "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol\","
            + "\"name\":\"A\","
            + "\"fullName\":\"A\","
            + "\"parameters\":["
            + "{"
            + "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\","
            + "\"name\":\"p\","
            + "\"fullName\":\"A.p\","
            + "\"type\":{"
            + "\"kind\":\"de.monticore.types.check.SymTypePrimitive\","
            + "\"primitiveName\":\"int\""
            + "}"
            + "}"
            + "],"
            + "\"spannedScope\":{"
            + "\"symbols\":["
            + "{"
            + "\"kind\":\"de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol\","
            + "\"name\":\"p\","
            + "\"fullName\":\"A.p\","
            + "\"types\":["
            + "{"
            + "\"kind\":\"de.monticore.types.check.SymTypePrimitive\","
            + "\"primitiveName\":\"int\""
            + "}"
            + "],"
            + "\"in\":true"
            + "}"
            + "]"
            + "}"
            + "}"
            + "]"
            + "}");
  }

}
