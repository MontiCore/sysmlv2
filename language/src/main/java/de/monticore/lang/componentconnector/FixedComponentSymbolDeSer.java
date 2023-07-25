package de.monticore.lang.componentconnector;

import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Anpassungen des {@link ComponentSymbolDeSer}, die eher früher als später nach MontiCore verschoben werden
 */
public class FixedComponentSymbolDeSer extends ComponentSymbolDeSer {

  public static final String SUBCOMPONENTS = "subcomponents";

  /**
   * die Addons korrekt verwendet
   */
  @Override
  public String serialize(@NonNull ComponentSymbol toSerialize, @NonNull CompSymbolsSymbols2Json s2j) {
    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());
    serializeSuperComponents(toSerialize.getSuperComponentsList(), s2j);
    // Nicht das Scope traversieren.
    // Begründung Vanilla-CompSymbols: "Da sind private Infos drin"
    // Begründung Montibelle: Wir entscheiden bei Adaptierten Symbolen lieber selbst was serialisiert wird und wie.
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());
    serializeAddons(toSerialize, s2j);
    p.endObject();
    return p.toString();
  }

  /**
   * Sollte das Gegenstück zu deserialzeAddons sein, war aber nicht implementiert.
   * Ausserdem fehlten die Subcomponents
   */
  @Override
  protected  void serializeAddons(ComponentSymbol toSerialize, CompSymbolsSymbols2Json s2j) {
    serializeParameters(toSerialize, s2j);
    serializePorts(toSerialize, s2j);
    serializeTypeParameters(toSerialize, s2j);
    serializeSubcomponents(toSerialize, s2j);
  }

  protected void serializeSubcomponents(@NonNull ComponentSymbol portOwner, @NonNull CompSymbolsSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(SUBCOMPONENTS);
    portOwner.getSubcomponents().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

}
