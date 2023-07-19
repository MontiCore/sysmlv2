package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector._ast.ASTParameterValue;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.KindOfComponentDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class MildInstanceSymbolDeSer extends MildInstanceSymbolDeSerTOP {
  private final KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override protected void serializeParameterValues(
      List<ASTParameterValue> parameterValues,
      ComponentConnectorSymbols2Json s2j)
  {
    // TODO
    Log.error("Implement me");
  }

  @Override protected void serializeType(CompKindExpression type, ComponentConnectorSymbols2Json s2j) {
    s2j.getJsonPrinter().memberJson("type", deSer.serializeAsJson((KindOfComponent) type));
  }

  @Override protected List<ASTParameterValue> deserializeParameterValues(JsonObject symbolJson) {
    // TODO
    Log.error("Implement me");
    return null;
  }

  @Override protected CompKindExpression deserializeType(JsonObject symbolJson) {
    // TODO
    Log.error("Implement me");
    return null;
  }
}
