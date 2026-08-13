package de.monticore.lang.componentconnector._symboltable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/* TODO CompSymbols DeSer hard-codes serialization of params, ports, typevars,
 *   effect chains in sub-scope. This functionality is not regenerated here and
 *   should be copy pasted until we can use only CompSymbols for trafo.
 */
public class MildComponentSymbolDeSer extends MildComponentSymbolDeSerTOP {

  protected final CompKindExpressionDeSer deSer = new CompKindExpressionDeSer();

  @Override
  protected void deserializeAddons(MildComponentSymbol symbol, JsonObject symbolJson) {
    symbol.getParameterList().forEach(symbol.getSpannedScope()::add);
    fillEffectChain(symbol, symbolJson);
  }

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                                ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentTypeSymbolDeSer.REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected void serializeConnectors(List<ASTConnector> connectors, ComponentConnectorSymbols2Json s2j) {
    // Wird nicht implementiert
  }

  @Override
  protected void serializeParameter(List<VariableSymbol> parameter, ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(ComponentTypeSymbolDeSer.PARAMETERS);
    parameter.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected void serializeSuperComponents(@NonNull List<CompKindExpression> superComponents,
                                          @NonNull ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentTypeSymbolDeSer.SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serialize(superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override protected List<CompKindExpression> deserializeRefinements(IComponentConnectorScope scope, JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.REFINEMENTS).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(refinements.size());

    for (JsonElement refinement : refinements) {
      result.add(deSer.deserialize(scope, refinement));
    }
    return result;
  }

  @Override
  protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<ASTConnector> deserializeConnectors(JsonObject symbolJson) {
    // Wird nicht implementiert
    Log.error("0xD0001 Attempted to deserialize connectors, but connectors are never serialized to begin with.");
    return null;
  }

  @Override
  protected List<VariableSymbol> deserializeParameter(JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.PARAMETERS).orElseGet(Collections::emptyList);
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
  protected List<CompKindExpression> deserializeSuperComponents(IComponentConnectorScope scope, JsonObject symbolJson) {
    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(ComponentTypeSymbolDeSer.SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(deSer.deserialize(scope, superComponent));
    }
    return result;
  }

  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void serializeEffectChains(
      Multimap<PortSymbol, PortSymbol> effectChains, ComponentConnectorSymbols2Json s2j) {
    if (effectChains == null) {
      return;
    }
    s2j.getJsonPrinter().beginObject("effectChain");
    for (PortSymbol key : effectChains.keys()) {
      s2j.getJsonPrinter().beginArray(key.getFullName());
      for (PortSymbol outPort : effectChains.get(key)) {
        s2j.getJsonPrinter().addToArray(new UserJsonString(outPort.getFullName()));
      }
      s2j.getJsonPrinter().endArray();
    }
    s2j.getJsonPrinter().endObject();
  }

  @Override
  protected Multimap<PortSymbol, PortSymbol> deserializeEffectChains(JsonObject symbolJson) {
    // Because we need the ports before being able to fill the chains, we only create the empty multimap here.
    return ArrayListMultimap.create();
  }

  @Override
  protected Multimap<PortSymbol, PortSymbol> deserializeEffectChains(
      IComponentConnectorScope scope, JsonObject symbolJson) {
    // Because we need the ports before being able to fill the chains, we only create the empty multimap here.
    return ArrayListMultimap.create();
  }

  protected void fillEffectChain(ComponentTypeSymbol symbol, JsonObject symbolJson) {
    Optional<JsonObject> chain = symbolJson.getObjectMemberOpt("effectChain");
    Multimap<PortSymbol, PortSymbol> effectMap = symbol.getEffectChains();
    for (Map.Entry<String, JsonElement> entry : chain.map(c -> c.getMembers().entrySet()).orElseGet(Collections::emptySet)) {
      List<PortSymbol> inPorts = symbol.getAllIncomingPorts().stream().filter(p -> p.getFullName().equals(entry.getKey())).toList();
      List<PortSymbol> outPorts = entry.getValue().getAsJsonArray().getValues().stream()
          .map(outPortName -> symbol.getSpannedScope().resolvePortMany(outPortName.toString()))
          .flatMap(Collection::stream).toList();
      for (PortSymbol inPort : inPorts) {
        effectMap.putAll(inPort, outPorts);
      }
    }
  }
}
