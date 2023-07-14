package deser;

import de.monticore.lang.componentconnector._symboltable.ComponentConnectorSymbols2Json;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbolDeSer;
import de.monticore.lang.componentconnector._visitor.ComponentConnectorHandler;
import de.monticore.lang.componentconnector._visitor.ComponentConnectorTraverser;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.mcbasics._symboltable.MCBasicsDeSer;
import de.monticore.mcbasics._symboltable.MCBasicsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsDeSer;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsDeSer;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PortSymbolDeserTest extends NervigeSymboltableTests {

  @Test
  public void testPorts() throws IOException {
    var as = process("part def A { port b: B; } port def B { in attribute b: boolean; }");
    fixSerialization();

    var comp = as.resolveComponent("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
        + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\",\"name\":\"A\",\"ports\":"
        + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\",\"name\":\"b.b\",\"type\":"
        + "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"boolean\"},\"incoming\":true,"
        + "\"timing\":\"timed\",\"stronglyCausal\":true}]}]}");
  }

  @Disabled
  @Test
  public void testConjugatedPorts() throws IOException {
    var as = process("part def A { port b: ~B; } port def B { in attribute b: boolean; }");
    fixSerialization();

    var comp = as.resolveComponent("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
        + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\",\"name\":\"A\",\"ports\":"
        + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\",\"name\":\"b.b\",\"type\":"
        + "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"boolean\"},\"incoming\":false,"
        + "\"timing\":\"timed\",\"stronglyCausal\":true}]}]}");
  }
}
