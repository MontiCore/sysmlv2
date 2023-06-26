package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.mcbasics._symboltable.MCBasicsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComponentSymbolDeserTest extends NervigeSymboltableTests {

  /**
   * Prüft eine simple Serialisierung eines adaptierten MildComponentSymbols
   * @throws IOException
   */
  @Test
  public void testSimpleSerialization() throws IOException {
    var as = process("part def A;");

    // Serialisierung nach SysML
    var st = new SysMLv2Symbols2Json().serialize(as);
    assertThat(st).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
        + "[{\"kind\":\"de.monticore.lang.sysmlparts._symboltable.PartDefSymbol\",\"name\":\"A\","
        + "\"requirementType\":\"UNKNOWN\"}]}");

    // Setup eines Scopes aus MildComponentSymbols
    var comp = as.resolveComponent("A").get();
    var s = SysMLv2Mill.artifactScope();
    s.add(comp);

    // Serialisierung nach MildComponent
    var mild_st = new SysMLv2Symbols2Json().serialize(s);
    assertThat(mild_st).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
        + "[{\"kind\":\"de.monticore.lang.componentconnector._symboltable.MildComponentSymbol\",\"name\":\"A\"}]}");

    // TODO Versuche hier die Default-Ser von CompSymbols zu benutzen

    // Das ist leer
    var comp_st = new CompSymbolsSymbols2Json().serialize(s);
    //assertThat(comp_st).isEqualTo("...");

    // Das knallt
    var traverser = SysMLv2Mill.inheritanceTraverser();
    var printer = new JsonPrinter();
    traverser.add4CompSymbols(new CompSymbolsSymbols2Json(traverser, printer));
    traverser.add4BasicSymbols(new BasicSymbolsSymbols2Json(traverser, printer));
    traverser.add4MCBasics(new MCBasicsSymbols2Json(traverser, printer));
    //TODO var comp_st1 = new SysMLv2Symbols2Json(traverser, printer).serialize(s);
    //assertThat(comp_st1).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
    //    + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\",\"name\":\"A\"}]}");
  }

}
