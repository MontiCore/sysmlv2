package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlv2._ast.ASTConfiguration;
import de.monticore.lang.sysmlv2._ast.ASTEventTransition;
import de.monticore.lang.sysmlv2._ast.ASTStateSpace;
import de.monticore.lang.sysmlv2._ast.ASTTransition;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2._symboltable.EventAutomatonSymbolDeSerTOP;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class EventAutomatonSymbolDeSer extends EventAutomatonSymbolDeSerTOP {
  @Override
  protected void serializeStateSpace(
      ASTStateSpace stateSpace,
      SysMLv2Symbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeInitialConfiguration(
      List<ASTConfiguration> initialConfiguration,
      SysMLv2Symbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeEventTransitions(
      List<ASTEventTransition> eventTransitions,
      SysMLv2Symbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeTickTransitions(
      List<ASTTransition> tickTransitions,
      SysMLv2Symbols2Json s2j)
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
