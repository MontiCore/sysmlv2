package de.monticore.lang.componentconnector._symboltable;

import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class MildPortSymbolDeSer extends MildPortSymbolDeSerTOP {
  @Override
  protected void serializeType(SymTypeExpression type, ComponentConnectorSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "type", type);
  }

  @Override
  protected void serializeTiming(Timing timing, ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().member("timing", timing.getName());
  }

  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
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
}
