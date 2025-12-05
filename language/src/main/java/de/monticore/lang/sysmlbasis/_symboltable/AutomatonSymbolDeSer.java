package de.monticore.lang.sysmlbasis._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTConfiguration;
import de.monticore.lang.sysmlbasis._ast.ASTEventTransition;
import de.monticore.lang.sysmlbasis._ast.ASTStateSpace;
import de.monticore.lang.sysmlbasis._ast.ASTTransition;
import de.monticore.lang.sysmlbasis._symboltable.AutomatonSymbolDeSerTOP;
import de.monticore.lang.sysmlbasis._symboltable.SysMLBasisSymbols2Json;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class AutomatonSymbolDeSer extends AutomatonSymbolDeSerTOP {
  @Override
  protected void serializeStateSpace(
      ASTStateSpace stateSpace,
      SysMLBasisSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeInitialConfiguration(
      List<ASTConfiguration> initialConfiguration,
      SysMLBasisSymbols2Json s2j)
  {
    // not implemented
  }

  @Override
  protected void serializeTransitions(
      List<ASTTransition> tickTransitions,
      SysMLBasisSymbols2Json s2j)
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
  protected List<ASTTransition> deserializeTransitions(JsonObject symbolJson) {
    return null;
  }
}
