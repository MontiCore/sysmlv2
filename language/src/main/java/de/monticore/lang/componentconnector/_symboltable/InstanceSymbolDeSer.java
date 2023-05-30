package de.monticore.lang.componentconnector._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.KindOfComponentDeSer;

public class InstanceSymbolDeSer extends InstanceSymbolDeSerTOP{
  // can't use SubcomponentSymbolDeSer or FullCompKindExprDeSer directly
  private KindOfComponentDeSer deSer = new KindOfComponentDeSer();

  @Override
  protected void serializeType(CompKindExpression type, ComponentConnectorSymbols2Json s2j) {
    deSer.serializeAsJson((KindOfComponent) type);
  }

  @Override
  protected CompKindExpression deserializeType(JsonObject symbolJson) {
    return deSer.deserialize(symbolJson);
  }
}
