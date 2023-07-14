package symboltable;

import de.monticore.lang.componentconnector._symboltable.ComponentConnectorSymbols2Json;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbolDeSer;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract public class NervigeSymboltableTests {

  protected static SysMLv2Tool tool;

  @BeforeAll
  public static void setup() {
    tool = new SysMLv2Tool();
  }

  @BeforeEach
  public void init() {
    // Important: Resets everything, including crude DeSer setup!
    tool.init();
  }

  /** Erledigt das nervige Geraffel */
  protected ISysMLv2ArtifactScope process(String model) throws IOException {
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var as = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);
    return as;
  }

  /** Vorläufiger Ablageort für ST-Serialization Geraffel */
  protected void fixSerialization() {
    MildComponentSymbolDeSer myTypeSymbolDeSer = new MildComponentSymbolDeSer() {
      ComponentSymbolDeSer delegate = new ComponentSymbolDeSer();
      @Override
      public String serialize (MildComponentSymbol toSerialize, ComponentConnectorSymbols2Json s2j){
        return delegate.serialize(toSerialize, new CompSymbolsSymbols2Json(s2j.getTraverser(), s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put("de.monticore.lang.componentconnector._symboltable.MildComponentSymbol", myTypeSymbolDeSer);
  }

}
