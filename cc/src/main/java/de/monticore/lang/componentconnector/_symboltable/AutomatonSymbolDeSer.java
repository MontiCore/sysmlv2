package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector._ast.ASTConfiguration;
import de.monticore.lang.componentconnector._ast.ASTEventTransition;
import de.monticore.lang.componentconnector._ast.ASTStateSpace;
import de.monticore.lang.componentconnector._ast.ASTTransition;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class AutomatonSymbolDeSer extends AutomatonSymbolDeSerTOP {
  @Override
  protected void serializeStateSpace(
      ASTStateSpace stateSpace,
      ComponentConnectorSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeInitialConfiguration(
      List<ASTConfiguration> initialConfiguration,
      ComponentConnectorSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeEventTransitions(
      List<ASTEventTransition> eventTransitions,
      ComponentConnectorSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeTickTransitions(
      List<ASTTransition> tickTransitions,
      ComponentConnectorSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected ASTStateSpace deserializeStateSpace(JsonObject symbolJson) {
    return null;
  }

  @Override
  protected List<ASTConfiguration> deserializeInitialConfiguration(JsonObject symbolJson) {
    return null;
  }

  @Override
  protected List<ASTEventTransition> deserializeEventTransitions(JsonObject symbolJson) {
    return null;
  }

  @Override
  protected List<ASTTransition> deserializeTickTransitions(JsonObject symbolJson) {
    return null;
  }
}
