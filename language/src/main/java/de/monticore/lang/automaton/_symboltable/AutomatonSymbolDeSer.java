package de.monticore.lang.automaton._symboltable;

import de.monticore.lang.automaton._ast.ASTConfiguration;
import de.monticore.lang.automaton._ast.ASTEventTransition;
import de.monticore.lang.automaton._ast.ASTStateSpace;
import de.monticore.lang.automaton._ast.ASTTransition;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class AutomatonSymbolDeSer extends AutomatonSymbolDeSerTOP {
  @Override
  protected void serializeStateSpace(ASTStateSpace stateSpace, AutomatonSymbols2Json s2j) {
    // not implemented
  }

  @Override
  protected void serializeInitialConfiguration(List<ASTConfiguration> initialConfiguration, AutomatonSymbols2Json s2j) {
    // not implemented
  }

  @Override
  protected void serializeEventTransitions(List<ASTEventTransition> eventTransitions, AutomatonSymbols2Json s2j) {
    // not implemented
  }

  @Override
  protected void serializeTickTransitions(List<ASTTransition> tickTransitions, AutomatonSymbols2Json s2j) {
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
