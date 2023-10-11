package symboltable;

import de.monticore.lang.componentconnector._symboltable.ComponentConnectorSymbols2Json;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbolDeSer;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.PortSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.KindOfComponentDeSer;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import deser.SubcomponentSymbolDeserTest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract public class NervigeSymboltableTests {

  protected static SysMLv2Tool tool;

  @BeforeAll
  public static void _setup() {
    tool = new SysMLv2Tool();
  }

  @BeforeEach
  public void init() {
    // Important: Resets everything, including crude DeSer setup!
    tool.init();
    LogStub.init();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
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
    MildComponentSymbolDeSer myComponentSymbolDeSer = new MildComponentSymbolDeSer() {
      ComponentSymbolDeSer delegate = new ComponentSymbolDeSer();
      @Override
      public String serialize (MildComponentSymbol toSerialize, ComponentConnectorSymbols2Json s2j){
        return delegate.serialize(toSerialize, new CompSymbolsSymbols2Json(s2j.getTraverser(), s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put("de.monticore.lang.componentconnector._symboltable.MildComponentSymbol", myComponentSymbolDeSer);

    MildPortSymbolDeSer myPortSymbolDeSer = new MildPortSymbolDeSer() {
      PortSymbolDeSer delegate = new PortSymbolDeSer();
      @Override
      public String serialize (MildPortSymbol toSerialize, ComponentConnectorSymbols2Json s2j){
        return delegate.serialize(toSerialize, new CompSymbolsSymbols2Json(s2j.getTraverser(), s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put("de.monticore.lang.componentconnector._symboltable.MildPortSymbol", myPortSymbolDeSer);

    // Den Teil verstehe ich nicht wirklich - wieso hat DS einen "FullCompKindExprDeSer" erfunden?
    // Ohne kann der SubcomponentSymbolDeSer seinen Job nicht erledigen. Man würde zwar erwarten, dass der korrekt
    // konfiguriert würde, aber... just MontiCore things.
    var fullCompKindDeSer = new FullCompKindExprDeSer() {
      private KindOfComponentDeSer delegate = new KindOfComponentDeSer();
      @Override
      public String serializeAsJson(@NonNull CompKindExpression toSerialize) {
        return delegate.serializeAsJson((KindOfComponent) toSerialize);
      }
      @Override
      public CompKindExpression deserialize(@NonNull JsonElement serialized) {
        return delegate.deserialize((JsonObject) serialized);
      }
    };

    MildInstanceSymbolDeSer mySubcomponentSymbolDeSer = new MildInstanceSymbolDeSer() {
      SubcomponentSymbolDeSer delegate = new SubcomponentSymbolDeSer(fullCompKindDeSer);
      @Override
      public String serialize (MildInstanceSymbol toSerialize, ComponentConnectorSymbols2Json s2j){
        return delegate.serialize(toSerialize, new CompSymbolsSymbols2Json(s2j.getTraverser(), s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put("de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol", mySubcomponentSymbolDeSer);

  }

}
