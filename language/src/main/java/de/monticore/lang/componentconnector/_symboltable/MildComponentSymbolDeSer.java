package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector.ComponentConnectorMill;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
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
  public static final String PARAMETERS = "parameters";
  public static final String PORTS = "ports";
  public static final String TYPE_PARAMETERS = "typeParameters";
  public static final String SUPER = "super";
  public static final String SUBCOMPONENTS = "subcomponents";
  public static final String REFINEMENTS = "refinements";

  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override
  protected  void serializeAddons(MildComponentSymbol toSerialize, ComponentConnectorSymbols2Json s2j) {
    serializeParameters(toSerialize, s2j);
    serializePorts(toSerialize, s2j);
    serializeTypeParameters(toSerialize, s2j);
    serializeSubcomponents(toSerialize, s2j);
  }

  @Override
  protected void deserializeAddons(MildComponentSymbol symbol, JsonObject symbolJson) {
    deserializeParameters(symbol, symbolJson);
    deserializePorts(symbol, symbolJson);
    deserializeTypeParameters(symbol, symbolJson);
  }

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

  protected void serializeParameters(@NonNull ComponentSymbol paramOwner, @NonNull ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PARAMETERS);
    paramOwner.getParameters().forEach(p -> p.accept(s2j.getTraverser()));
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

  protected void serializePorts(@NonNull MildComponentSymbol portOwner, @NonNull ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(PORTS);
    portOwner.getPorts().forEach(p -> p.accept(s2j.getTraverser()));
    printer.endArray();
  }

  protected void serializeTypeParameters(@NonNull MildComponentSymbol typeParamOwner, ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(TYPE_PARAMETERS);
    typeParamOwner.getTypeParameters().forEach(tp -> tp.accept(s2j.getTraverser()));
    printer.endArray();
  }

  protected void serializeSubcomponents(@NonNull MildComponentSymbol portOwner, @NonNull ComponentConnectorSymbols2Json s2j) {
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(SUBCOMPONENTS);
    portOwner.getSubcomponents().forEach(p -> p.accept(s2j.getTraverser()));
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
    Log.error("0xD0001 Attempted to deserialize connectors, but connectors are never serialized to begin with.");
    return null;
  }

  /**
   * @param paramOwnerJson the component which owns the parameters, encoded as JSON.
   */
  protected void deserializeParameters(@NonNull MildComponentSymbol paramOwner, @NonNull JsonObject paramOwnerJson) {
    final String varSerializeKind = VariableSymbol.class.getCanonicalName();

    List<JsonElement> params = paramOwnerJson.getArrayMemberOpt(PARAMETERS).orElseGet(Collections::emptyList);

    for (JsonElement param : params) {
      String paramJsonKind = JsonDeSers.getKind(param.getAsJsonObject());
      if (paramJsonKind.equals(varSerializeKind)) {
        ISymbolDeSer deSer = ComponentConnectorMill.globalScope().getSymbolDeSer(varSerializeKind);
        VariableSymbol paramSym = (VariableSymbol) deSer.deserialize(param.getAsJsonObject());

        paramOwner.getSpannedScope().add(paramSym);

      } else {
        Log.error(String.format(
            "0xD0101 Malformed json, parameter '%s' of unsupported kind '%s'",
            param.getAsJsonObject().getStringMember(JsonDeSers.NAME), paramJsonKind
        ));
      }
    }
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

  protected void deserializePorts(@NonNull MildComponentSymbol portOwner, @NonNull JsonObject paramOwnerJson) {
    final String portSerializeKind = PortSymbol.class.getCanonicalName();

    List<JsonElement> ports = paramOwnerJson.getArrayMemberOpt(PORTS).orElseGet(Collections::emptyList);

    for (JsonElement port : ports) {
      String portJasonKind = JsonDeSers.getKind(port.getAsJsonObject());
      if (portJasonKind.equals(portSerializeKind)) {
        ISymbolDeSer deSer = ComponentConnectorMill.globalScope().getSymbolDeSer(portSerializeKind);
        PortSymbol portSym = (PortSymbol) deSer.deserialize(port.getAsJsonObject());

        portOwner.getSpannedScope().add(portSym);

      } else {
        Log.error(String.format(
            "0xD0102 Malformed json, port '%s' of unsupported kind '%s'",
            port.getAsJsonObject().getStringMember(JsonDeSers.NAME), portJasonKind
        ));
      }
    }
  }

  protected void deserializeTypeParameters(@NonNull MildComponentSymbol typeParamOwner,
                                           @NonNull JsonObject typeParamOwnerJson) {
    final String typeVarSerializedKind = "de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol";

    List<JsonElement> typeParams =
        typeParamOwnerJson
            .getArrayMemberOpt(TYPE_PARAMETERS)
            .orElseGet(Collections::emptyList);

    for (JsonElement typeParam : typeParams) {
      String typeParamJsonKind = JsonDeSers.getKind(typeParam.getAsJsonObject());
      if (typeParamJsonKind.equals(typeVarSerializedKind)) {
        ISymbolDeSer deSer = ComponentConnectorMill.globalScope().getSymbolDeSer(typeVarSerializedKind);
        TypeVarSymbol typeParamSym = (TypeVarSymbol) deSer.deserialize(typeParam.getAsJsonObject());

        typeParamOwner.getSpannedScope().add(typeParamSym);
      } else {
        Log.error(String.format(
            "0xD0103 Malformed json, type parameter '%s' of unsupported kind '%s'",
            typeParam.getAsJsonObject().getStringMember(JsonDeSers.NAME), typeParamJsonKind
        ));

      }
    }
  }
}
