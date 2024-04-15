package symboltable;

import de.monticore.lang.automaton._symboltable.AutomatonSymbols2Json;
import de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbol;
import de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbolDeSer;
import de.monticore.lang.componentconnector.SerializationUtil;
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

abstract public class NervigeSymboltableTests extends SerializationUtil {

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

}
