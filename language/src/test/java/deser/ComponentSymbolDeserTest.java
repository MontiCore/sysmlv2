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
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolDeSer;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Diese Tests dokumentieren die Odyssee um adaptierte SysML-Symbole nach
 * CompSymbols zu serialisieren. Die Motivation ist, dass es externen (i.e.,
 * nicht direkt in Kenntnis der SysML-Interna) Sprachen und Werkzeugen
 * ermöglicht werden soll Referenzen auf SysML-Elemente aus dem Dunstkreis des
 * Systems Engineering (Components & Ports) zu prüfen.
 * <p>
 * Die @Disbled'ten Tests sind gescheiterte Versuche. Die letzte beiden sind die
 * möglichen Lösungen, wobei die letzte die einzige vertretbare Implementierung
 * ist.
 */
public class ComponentSymbolDeserTest extends NervigeSymboltableTests {

  @Test
  public void forSysml() throws IOException {
    var as = process("part def A;");
    var st = new SysMLv2Symbols2Json().serialize(as);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.lang.sysmlparts._symboltable"
            + ".PartDefSymbol\",\"name\":\"A\","
            + "\"fullName\":\"A\","
            + "\"requirementType\":\"UNKNOWN\"}]}");
  }

  @Test
  public void forMild() throws IOException {
    var as = process("part def A;");

    // Setup eines Scopes aus MildComponentSymbols
    var comp = as.resolveComponentType("A").get();
    var s = SysMLv2Mill.artifactScope();
    s.add(comp);

    // Serialisierung nach MildComponent
    var mild_st = new SysMLv2Symbols2Json().serialize(s);
    var expected ="{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
        + "\"symbols\":"
        + "[{\"kind\":\"de.monticore.lang.componentconnector._symboltable"
        + ".MildComponentSymbol\",\"name\":\"A\","
        + "\"fullName\":\"A\""
        + "}]}";
    assertThat(mild_st).isEqualTo(expected);
  }

  // TODO Versuche ab hier die Default-Ser von CompSymbols zu benutzen
  @Disabled
  @Test
  public void forComponent_Naive() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    // Hier kommt nichts an, weil die Symbole in ihrem wahren Typen -
    // MildComponentSymbol - besucht werden.
    var st = new CompSymbolsSymbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentTypeSymbol\",\"name\":\"A\"}]}");
  }

  @Disabled
  @Test
  public void forComponent_NaiveWithInheritance() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    // Deswegen nehme ich einen Inheritance Traverser und "nur" die nötigsten
    // Visitoren
    var traverser = SysMLv2Mill.inheritanceTraverser();
    var printer = new JsonPrinter();

    var compSymbolsSymbols2Json = new CompSymbolsSymbols2Json(traverser,
        printer);
    traverser.add4CompSymbols(compSymbolsSymbols2Json);

    var basicSymbolsSymbols2Json = new BasicSymbolsSymbols2Json(traverser,
        printer);
    traverser.add4BasicSymbols(basicSymbolsSymbols2Json);

    var mcBasicsSymbols2Json = new MCBasicsSymbols2Json(traverser, printer);
    traverser.add4MCBasics(mcBasicsSymbols2Json);

    var st = new SysMLv2Symbols2Json(traverser, printer).serialize(
        artifact); // <--- Hier knallts
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentTypeSymbol\",\"name\":\"A\"}]}");
  }

  @Disabled
  @Test
  public void forComponent_WorkaroundAHe() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    var traverser = SysMLv2Mill.inheritanceTraverser();
    var printer = new JsonPrinter();

    // Workaround von @AHe: scopeDeSer setzen
    var compSymbolsSymbols2Json = new CompSymbolsSymbols2Json(traverser,
        printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new CompSymbolsDeSer();
      }
    };
    traverser.add4CompSymbols(compSymbolsSymbols2Json);

    var basicSymbolsSymbols2Json = new BasicSymbolsSymbols2Json(traverser,
        printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new BasicSymbolsDeSer();
      }
    };
    traverser.add4BasicSymbols(basicSymbolsSymbols2Json);

    var mcBasicsSymbols2Json = new MCBasicsSymbols2Json(traverser, printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new MCBasicsDeSer();
      }
    };
    traverser.add4MCBasics(mcBasicsSymbols2Json);

    // Hier sind für jeden Visitor ein Scope zuviel drin und kein Artifact,
    // wie AHe selber bemerkt
    var st = new SysMLv2Symbols2Json(traverser, printer).serialize(artifact);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentTypeSymbol\",\"name\":\"A\"}]}");
  }

  @Test
  public void forComponent_WorkaroundAHe_WithMPfHandler() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    // Ohne Inheritance, weil die schein Scopes zuviel zu produzieren
    var traverser = SysMLv2Mill.traverser();
    var printer = new JsonPrinter();

    // Ohne Inheritance-Traverse ging es vielleicht mit Handler?
    traverser.setComponentConnectorHandler(new ComponentConnectorHandler() {
      protected ComponentConnectorTraverser t = traverser;

      @Override
      public ComponentConnectorTraverser getTraverser() {
        return t;
      }

      @Override
      public void setTraverser(ComponentConnectorTraverser traverser) {
        t = traverser;
      }

      // Caste und visitiere damit im Super-Type
      @Override
      public void handle(MildComponentSymbol node) {
        getTraverser().visit((ComponentTypeSymbol) node);
        // Direkt Traverse, damit Inheritance-Problem nicht wieder zuschlägt
        //ComponentConnectorHandler.super.handle(node);
        ComponentConnectorHandler.super.traverse(node);
        getTraverser().endVisit((ComponentTypeSymbol) node);
      }
    });

    // Braucht noch was für ArtifactScopes
    var sysMLv2Symbols2Json = new SysMLv2Symbols2Json(traverser, printer);
    traverser.add4SysMLv2(sysMLv2Symbols2Json);

    // Workaround von @AHe wie bisher
    var compSymbolsSymbols2Json = new CompSymbolsSymbols2Json(traverser,
        printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new CompSymbolsDeSer();
      }
    };
    traverser.add4CompSymbols(compSymbolsSymbols2Json);

    var basicSymbolsSymbols2Json = new BasicSymbolsSymbols2Json(traverser,
        printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new BasicSymbolsDeSer();
      }
    };
    traverser.add4BasicSymbols(basicSymbolsSymbols2Json);

    var mcBasicsSymbols2Json = new MCBasicsSymbols2Json(traverser, printer) {
      @Override
      public void init() {
        super.init();
        scopeDeSer = new MCBasicsDeSer();
      }
    };
    traverser.add4MCBasics(mcBasicsSymbols2Json);

    // Das klappt! Ist nur sehr viel repetitives Setup
    var st = new SysMLv2Symbols2Json(traverser, printer).serialize(
        artifact); // <--- Hier knallts
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentTypeSymbol\",\"name\":\"A\",\"fullName\":\"A\"}]}");
  }

  @Test
  public void forComponent_WorkaroundAHe2() throws IOException {
    var as = process("part def A;");

    var comp = as.resolveComponentType("A").get();
    var artifact = SysMLv2Mill.artifactScope();
    artifact.add(comp);

    // Zweiter Versuch von AHe
    MildComponentSymbolDeSer myTypeSymbolDeSer =
        new MildComponentSymbolDeSer() {
          ComponentTypeSymbolDeSer delegate = new ComponentTypeSymbolDeSer();

          @Override
          public String serialize(MildComponentSymbol toSerialize,
                                  ComponentConnectorSymbols2Json s2j) {
            return delegate.serialize(toSerialize,
                new CompSymbolsSymbols2Json(s2j.getTraverser(),
                    s2j.getJsonPrinter()));
          }
        };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put(
            "de.monticore.lang.componentconnector._symboltable"
                + ".MildComponentSymbol",
            myTypeSymbolDeSer);

    // Klappt auch, aber ziemlich Spaghetti
    var st = new SysMLv2Symbols2Json().serialize(artifact);
    assertThat(st).isEqualTo(
        "{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\","
            + "\"symbols\":"
            + "[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable"
            + ".ComponentTypeSymbol\",\"name\":\"A\",\"fullName\":\"A\"}]}");
  }
}
