package de.monticore.lang.automaton._symboltable;

import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.KindOfComponentDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Copied from ComponentSymbolDeSer */
public class ExtendedMildComponentSymbolDeSer extends ExtendedMildComponentSymbolDeSerTOP {

  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override
  protected void deserializeAddons(ExtendedMildComponentSymbol symbol, JsonObject symbolJson) {
    symbol.getParameterList().forEach(symbol.getSpannedScope()::add);
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                      AutomatonSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentSymbolDeSer.REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serializeAsJson((KindOfComponent) superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected void serializeConnectors(List<ASTConnector> connectors, AutomatonSymbols2Json s2j) {
    // Wird nicht implementiert
  }

  @Override
  protected void serializeParameter(List<VariableSymbol> parameter, AutomatonSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(ComponentSymbolDeSer.PARAMETERS);
    parameter.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected void serializeSuperComponents(@NonNull List<CompKindExpression> superComponents,
                                          @NonNull AutomatonSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentSymbolDeSer.SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serializeAsJson((KindOfComponent) superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(ComponentSymbolDeSer.REFINEMENTS).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(refinements.size());

    for (JsonElement refinement : refinements) {
      result.add(deSer.deserialize((JsonObject) refinement));
    }
    return result;
  }

  @Override protected List<ASTConnector> deserializeConnectors(JsonObject symbolJson) {
    // Wird nicht implementiert
    Log.error("0xD0001 Attempted to deserialize connectors, but connectors are never serialized to begin with.");
    return null;
  }

  @Override
  protected List<VariableSymbol> deserializeParameter(JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(ComponentSymbolDeSer.PARAMETERS).orElseGet(Collections::emptyList);
    List<VariableSymbol> parameterResult = new ArrayList<>(params.size());

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      ISymbolDeSer<?, ?> deSer = CompSymbolsMill.globalScope().getSymbolDeSer(paramJsonKind);
      if (deSer != null && deSer.getSerializedKind().equals(varSerializeKind)) {
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());
        parameterResult.add(paramSym);
      } else {
        Log.error(String.format(
            "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
            param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }
    return parameterResult;
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {
    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(ComponentSymbolDeSer.SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(deSer.deserialize((JsonObject) superComponent));
    }
    return result;
  }
}
