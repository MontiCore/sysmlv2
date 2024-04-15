/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlbasis._symboltable;

import de.monticore.lang.sysmlbasis.symboltable.SerializationUtil;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class AnonymousUsageSymbolDeSer extends AnonymousUsageSymbolDeSerTOP {

  @Override
  protected void serializeTypes(List<SymTypeExpression> types, SysMLBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "types", types);
  }

  @Override
  protected List<SymTypeExpression> deserializeTypes(JsonObject symbolJson) {
    return SerializationUtil.deserializeListMember("types", symbolJson, SysMLv2Mill.globalScope());
  }

}
