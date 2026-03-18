package de.monticore.lang.componentconnector._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class RequirementSymbolDeSer extends RequirementSymbolDeSerTOP {

  @Override
  protected void serializeAssumptions(List<ASTExpression> assumptions,
                                      ComponentConnectorSymbols2Json s2j) {
    // not implemented
  }

  @Override
  protected void serializeGuarantee(ASTExpression guarantee,
                                    ComponentConnectorSymbols2Json s2j) {
    // not implemented
  }

  @Override
  protected List<ASTExpression> deserializeAssumptions(JsonObject symbolJson) {
    // not implemented
    return List.of();
  }

  @Override
  protected ASTExpression deserializeGuarantee(JsonObject symbolJson) {
    // not implemented
    return null;
  }
}
