package de.monticore.lang.sysmlbasis._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlbasis._symboltable.SysMLBasisSymbols2Json;
import de.monticore.lang.sysmlbasis._symboltable.MildSpecificationSymbolDeSerTOP;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

/**
 * We do not serialize or deserialze the assumptions and predicates. They only exist as common interface between
 * systems engineering languages such as MontiArc and SysML to fascilitate language-agnostic processing by common
 * tools such as the SysML-2-Isabelle-Transformer.
 */
public class MildSpecificationSymbolDeSer extends MildSpecificationSymbolDeSerTOP {
  @Override
  protected void serializeAssumptions(List<ASTExpression> assumptions, SysMLBasisSymbols2Json s2j) {
    // Does nothing
  }

  @Override
  protected void serializePredicates(List<ASTExpression> predicates, SysMLBasisSymbols2Json s2j) {
    // Does nothing
  }

  @Override protected List<ASTExpression> deserializeAssumptions(JsonObject symbolJson) {
    // We never serialized to begin with
    return null;
  }

  @Override protected List<ASTExpression> deserializePredicates(JsonObject symbolJson) {
    // We never serialized to begin with
    return null;
  }
}
