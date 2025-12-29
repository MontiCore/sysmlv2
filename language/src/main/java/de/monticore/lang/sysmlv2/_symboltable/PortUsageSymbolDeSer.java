/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class PortUsageSymbolDeSer extends PortUsageSymbolDeSerTOP {
  @Override
  protected void serializeTypes(List<SymTypeExpression> types, SysMLPartsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "types", types);
  }

  @Override
  protected List<SymTypeExpression> deserializeTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember( "types", symbolJson);
  }

  @Override
  protected void serializeConjugatedTypes(List<SymTypeExpression> conjugatedTypes, SysMLPartsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "conjugatedTypes", conjugatedTypes);
  }
  @Override
  protected List<SymTypeExpression> deserializeConjugatedTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember( "conjugatedTypes", symbolJson);
  }
}
