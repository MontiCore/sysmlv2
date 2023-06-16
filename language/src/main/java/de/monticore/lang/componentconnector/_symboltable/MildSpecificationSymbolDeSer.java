package de.monticore.lang.componentconnector._symboltable;

import de.monticore.symbols.compsymbols._symboltable.Timing;
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
import java.util.Optional;

/* TODO CompSymbols DeSer hard-codes serialization of params, ports, typevars in sub-scope. This functionality is not regenerated
    here and should be copy pasted until we can use only CompSymbols for trafo.
 */
public class MildSpecificationSymbolDeSer extends MildSpecificationSymbolDeSerTOP {
  // can't use SubcomponentSymbolDeSer or FullCompKindExprDeSer directly
  public static final String SUPER = "super";
  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();
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
  protected void serializeRefinementStart(Optional<CompKindExpression> refinementStart, ComponentConnectorSymbols2Json s2j) {
    refinementStart.ifPresent(compKindExpression -> s2j.getJsonPrinter().memberJson("refinementStart",
        deSer.serializeAsJson((KindOfComponent) compKindExpression)));
  }

  @Override
  protected Optional<CompKindExpression> deserializeRefinementStart(JsonObject symbolJson) {
    if(symbolJson.hasMember("refinementStart")){
      return Optional.of(deSer.deserialize(symbolJson.getObjectMember("refinementStart")));
    }
    else{
      return java.util.Optional.empty();
    }
  }

  @Override
  protected void serializeTiming(Timing timing, ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().member("timing", timing.getName());
  }

  @Override
  protected Timing deserializeTiming(JsonObject symbolJson) {
    String value = symbolJson.getStringMember("timing");
    Optional<Timing> timing = Timing.of(value);

    if (timing.isPresent()) {
      return timing.get();
    } else {
      Log.error("0xD0100 Malformed json, unsupported timing '" + value + "'");
      return Timing.DEFAULT;
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
}
