package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class AttributeUsageSymbolDeSer extends AttributeUsageSymbolDeSerTOP {
  @Override
  protected void serializeTypes(List<SymTypeExpression> types, SysMLPartsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "types", types);
  }

  @Override
  protected List<SymTypeExpression> deserializeTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember( "types", symbolJson);
  }

  @Override
  protected void serializeDirection(ASTSysMLFeatureDirection direction, SysMLPartsSymbols2Json s2j) {
    s2j.getJsonPrinter().member("direction", direction.name());
  }

  @Override
  protected ASTSysMLFeatureDirection deserializeDirection(JsonObject symbolJson) {
    return ASTSysMLFeatureDirection.valueOf(symbolJson.getStringMember("direction"));
  }
}
