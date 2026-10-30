package serialization;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml.KerMLTool;
import de.monticore.lang.kerml._symboltable.KerMLSymbols2Json;
import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.lang.kerml.DatatypeAsTypeSymbolDeSer;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet die Implementierung der Serialisierung von KerML-Symbolen zu
 * BasicSymbols, spezifisch TypeSymbols
 */
public class KerML2BasicSymbolsTest {

  @Test
  public void testSerializeType() throws IOException {
    LogStub.init();
    var tool = new KerMLTool();
    tool.init();

    var ast = KerMLMill.parser().parse_String("datatype Test;").get();
    var as = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    // Trick von Hendrik Weskamp
    KerMLMill.globalScope().putSymbolDeSer(
        DatatypeSymbol.class.getName(), new DatatypeAsTypeSymbolDeSer());

    var st = new KerMLSymbols2Json().serialize(as);

    assertThat(st).isEqualTo("{\""
        + "generated-using\":\"www.MontiCore.de technology\","
        + "\"name\":\"Test\","
        + "\"symbols\":[{\""
        + "kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeSymbol\","
        + "\"name\":\"Test\","
        + "\"fullName\":\"Test\"}]}");
  }

  /**
   * Mit echten Adaptern und ohne dirty Tricks
   */
  @Test
  public void testSerializeType2() throws IOException {
    LogStub.init();
    var tool = new KerMLTool();
    tool.init();

    var ast = KerMLMill.parser().parse_String("datatype Test;").get();
    var as = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    // Lösung von AHe und MPf aus ComponentSymbolDeserTest
    // 1. Adaptiert Symbole (Adapter schreiben & registrieren)
    // 2. Scope manuell erstellen
    // 3. Das Scope serialisieren
    var typeSym = as.resolveType("Test").get();
    var scope = KerMLMill.artifactScope();
    scope.add(typeSym);
    var st = new KerMLSymbols2Json().serialize(scope);

    assertThat(st).isEqualTo("{\""
        + "generated-using\":\"www.MontiCore.de technology\","
        + "\"name\":\"Test\","
        + "\"symbols\":[{\""
        + "kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeSymbol\","
        + "\"name\":\"Test\","
        + "\"fullName\":\"Test\"}]}");
  }


  /**
   * Mit echten Adaptern und ohne dirty Tricks
   */
  @Test
  public void testSerializeTypeWithAnythingFeature() throws IOException {
    LogStub.init();
    var tool = new KerMLTool();
    tool.init();

    var ast = KerMLMill.parser()
        .parse_String("datatype Anything; datatype Test { feature a: Anything; }").get();
    var as = tool.createSymbolTable(ast);
    // KerML kann keinen Top-Level-Namen bestimmen
    as.setName("Test");
    tool.completeSymbolTable(ast);

    var typeSym = as.resolveType("Test").get();
    var scope = KerMLMill.artifactScope();
    scope.add(typeSym);
    var st = new KerMLSymbols2Json().serialize(scope);

    assertThat(st).isEqualTo("{\""
        + "generated-using\":\"www.MontiCore.de technology\","
        + "\"name\":\"Test\","
        + "\"symbols\":[{\""
        + "kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeSymbol\","
        + "\"name\":\"Test\","
        + "\"fullName\":\"Test\","
        + "\"spannedScope\":{"
        + "\"symbols\":[{"
        + "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\","
        + "\"name\":\"a\","
        + "\"fullName\":\"Test.a\""
        + "}]}}]}");
  }
}
