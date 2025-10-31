package de.monticore.lang.componentconnector._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._ast.ASTComponentType;
import de.monticore.symboltable.serialization.json.JsonObject;

public class RequirementSymbolDeSer extends RequirementSymbolDeSerTOP {

  @Override
  protected void serializeConstraint(ASTExpression constraint,
                                     ComponentConnectorSymbols2Json s2j) {
  }

  @Override
  protected ASTExpression deserializeConstraint(JsonObject symbolJson) {
    return null;
  }
}
