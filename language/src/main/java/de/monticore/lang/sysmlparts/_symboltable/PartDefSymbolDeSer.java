package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class PartDefSymbolDeSer extends PartDefSymbolDeSerTOP {

  @Override
  protected void serializeDirectRefinements(List<SymTypeExpression> refinementFQNs, SysMLPartsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "directRefinements", refinementFQNs);
  }

  @Override
  protected void serializeRequirementType(ASTSysMLReqType requirementType, SysMLPartsSymbols2Json s2j) {
    s2j.getJsonPrinter().member("requirementType", requirementType.name().toString());
  }

  @Override
  protected List<SymTypeExpression> deserializeDirectRefinements(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember( "directRefinements", symbolJson);
  }

  @Override
  protected ASTSysMLReqType deserializeRequirementType(JsonObject symbolJson) {
    var json = symbolJson.getMember("requirementType");
    if (!json.isJsonNull() && json.isJsonString()){
      return ASTSysMLReqType.valueOf(json.getAsJsonString().getValue());
    }
    return ASTSysMLReqType.UNKNOWN;
  }
}
