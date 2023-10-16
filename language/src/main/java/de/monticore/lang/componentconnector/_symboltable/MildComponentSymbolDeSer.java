package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
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

/* TODO CompSymbols DeSer hard-codes serialization of params, ports, typevars in sub-scope. This functionality is not regenerated
    here and should be copy pasted until we can use only CompSymbols for trafo.
 */
public class MildComponentSymbolDeSer extends MildComponentSymbolDeSerTOP{
  // can't use SubcomponentSymbolDeSer or FullCompKindExprDeSer directly
  public static final String SUPER = "super";
  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                                ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ComponentSymbolDeSer.REFINEMENTS);
    for (CompKindExpression superComponent : refinements) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serializeAsJson((KindOfComponent) superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected void serializeConnectors(List<ASTConnector> connectors, ComponentConnectorSymbols2Json s2j) {
    // Wird nicht implementiert
  }

  @Override
  protected void serializeParameters(List<VariableSymbol> parameters, ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();
    printer.beginArray(ComponentSymbolDeSer.PARAMETERS);
    parameters.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  @Override
  protected void serializeSuperComponents(@NonNull List<CompKindExpression> superComponents,
                                          @NonNull ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(SUPER);
    for (CompKindExpression superComponent : superComponents) {
      s2j.getJsonPrinter().addToArray(JsonElementFactory
          .createJsonString(deSer.serializeAsJson((KindOfComponent) superComponent)));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected void serializePorts(@NonNull List<PortSymbol> ports, @NonNull ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(ComponentSymbolDeSer.PORTS);
    ports.forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
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
    Log.error("Attempted to deserialize connectors, but connectors are never serialized to begin with.");
    return null;
  }

  /**
   * @param symbolJson the component which owns the parameters, encoded as JSON.
   */
  @Override
  protected List<VariableSymbol> deserializeParameters(@NonNull JsonObject symbolJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = symbolJson.getArrayMemberOpt(ComponentSymbolDeSer.PARAMETERS).orElseGet(Collections::emptyList);

    List<VariableSymbol> result = new ArrayList<>(params.size());

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      if (paramJsonKind.equals(varSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(varSerializeKind);
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());

        result.add(paramSym);

      } else {
        Log.error(String.format(
            "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
            param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }

    return result;
  }


  @Override
  protected List<CompKindExpression> deserializeSuperComponents(JsonObject symbolJson) {
    List<JsonElement> superComponents = symbolJson.getArrayMemberOpt(SUPER).orElseGet(Collections::emptyList);
    List<CompKindExpression> result = new ArrayList<>(superComponents.size());

    for (JsonElement superComponent : superComponents) {
      result.add(deSer.deserialize((JsonObject) superComponent));
    }
    return result;
  }

  @Override
  protected List<PortSymbol> deserializePorts(@NonNull JsonObject symbolJson) {
    final String portSerializeKind = PortSymbol.class.getCanonicalName();

    List<JsonElement> ports = symbolJson.getArrayMemberOpt(ComponentSymbolDeSer.PORTS).orElseGet(Collections::emptyList);

    List<PortSymbol> result = new ArrayList<>(ports.size());

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = CompSymbolsMill.globalScope().getSymbolDeSer(portSerializeKind);
        PortSymbol portSym = (PortSymbol) deSer.deserialize(port.getAsJsonObject());

        result.add(portSym);

      } else {
        Log.error(String.format(
            "0xD0102 Malformed json, port '%s' of unsupported kind '%s'",
            port.getAsJsonObject().getStringMember(JsonDeSers.NAME), portJasonKind
        ));
      }
    }

    return result;
  }
}
