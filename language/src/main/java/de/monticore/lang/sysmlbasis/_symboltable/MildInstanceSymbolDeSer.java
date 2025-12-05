package de.monticore.lang.sysmlbasis._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTParameterValue;
import de.monticore.lang.sysmlbasis._symboltable.SysMLBasisSymbols2Json;
import de.monticore.lang.sysmlbasis._symboltable.MildInstanceSymbolDeSerTOP;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class MildInstanceSymbolDeSer extends MildInstanceSymbolDeSerTOP {
  protected final CompKindExpressionDeSer deSer = new CompKindExpressionDeSer();

  @Override protected void serializeParameterValues(
      List<ASTParameterValue> parameterValues,
      SysMLBasisSymbols2Json s2j)
  {
    // TODO
    Log.error("0xTODO0 Implement me");
  }

  @Override protected void serializeType(CompKindExpression type, SysMLBasisSymbols2Json s2j) {
    s2j.getJsonPrinter().memberJson("type", deSer.serialize(type));
  }

  @Override protected List<ASTParameterValue> deserializeParameterValues(JsonObject symbolJson) {
    // TODO
    Log.error("0xTODO1 Implement me");
    return null;
  }

  @Override protected CompKindExpression deserializeType(JsonObject symbolJson) {
    // TODO
    Log.error("0xTODO2 Implement me");
    return null;
  }
}
