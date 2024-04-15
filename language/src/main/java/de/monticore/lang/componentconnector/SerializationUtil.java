package de.monticore.lang.componentconnector;

import de.monticore.lang.automaton._symboltable.AutomatonSymbols2Json;
import de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbol;
import de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.ComponentConnectorSymbols2Json;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbolDeSer;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
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
import org.checkerframework.checker.nullness.qual.NonNull;

public class SerializationUtil {

  /**
   * Sets up the neccessary serializers to be able to serialize adapted symbols as their base, e.g.,
   * MildComponentSymbols are printed as ComponentSymbols. This is done for cross-compatibility between tools.
   */
  public static void setupComponentConnectorSerialization() {
    ExtendedMildComponentSymbolDeSer myComponentSymbolDeSer = new ExtendedMildComponentSymbolDeSer() {
      ComponentSymbolDeSer delegate = new ComponentSymbolDeSer();
      @Override
      public String serialize (ExtendedMildComponentSymbol toSerialize, AutomatonSymbols2Json s2j){
        return delegate.serialize(toSerialize, new CompSymbolsSymbols2Json(s2j.getTraverser(), s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers()
        .put("de.monticore.lang.automaton._symboltable.ExtendedMildComponentSymbol", myComponentSymbolDeSer);

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

  /**
   * Class extracts PartDefs as ComponentSymbols and adds them to a new scope.
   */
  public static class PartDefExtractor implements SysMLPartsVisitor2 {
    private ISysMLv2Scope artifact;

    public PartDefExtractor(ISysMLv2Scope artifact) {
      this.artifact = artifact;
    }

    @Override
    public void visit(ASTPartDef node) {
      if(node.getEnclosingScope() instanceof ISysMLv2Scope) {
        var scope = (ISysMLv2Scope) node.getEnclosingScope();
        var component = scope.resolveComponent(node.getName());
        if(component.isPresent()) {
          artifact.add(component.get());
        }
      }
    }
  }

}
