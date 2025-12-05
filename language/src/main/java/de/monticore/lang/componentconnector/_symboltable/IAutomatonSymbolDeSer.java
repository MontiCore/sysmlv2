package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.componentconnector._ast.ASTConfiguration;
import de.monticore.lang.componentconnector._ast.ASTStateSpace;
import de.monticore.lang.componentconnector._ast.ASTTransition;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

/**
 * Tote Klasse, die nur zur Befriedigung von MontiCore existiert. MontiCore
 * sieht in der ComponentConnector-Grammatik nur "interface symbol", kann also
 * nicht zwischen den "echten Implementierungen" (Automaton, EventAutomaton) und
 * deren abstrakter Ober"klasse" (IAutomaton) unterscheiden. Und deswegen
 * generiert MontiCore für alle drei die DeSers, registriert sie im GlobalScope
 * und meckert, wenn ich die generierten abstrakten Klassen nicht per TOP
 * überschreibe.
 */
public class IAutomatonSymbolDeSer extends IAutomatonSymbolDeSerTOP {

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
  protected ASTStateSpace deserializeStateSpace(JsonObject symbolJson) {
    return null;
  }

  @Override
  protected List<ASTConfiguration> deserializeInitialConfiguration(JsonObject symbolJson) {
    return null;
  }

}
