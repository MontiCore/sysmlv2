package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbolDeSer;
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
  public static final String REFINEMENTS = "refinements";
  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override
  protected void serializeRefinements(List<CompKindExpression> refinements,
                                                ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(REFINEMENTS);
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

  @Override protected List<CompKindExpression> deserializeRefinements(JsonObject symbolJson) {
    List<JsonElement> refinements = symbolJson.getArrayMemberOpt(REFINEMENTS).orElseGet(Collections::emptyList);
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

  @Override protected List<VariableSymbol> deserializeParameters(JsonObject symbolJson) {
    // TODO
    Log.error("Not Implemented");
    return null;
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
