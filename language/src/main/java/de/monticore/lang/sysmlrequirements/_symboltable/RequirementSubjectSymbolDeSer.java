package de.monticore.lang.sysmlrequirements._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class RequirementSubjectSymbolDeSer extends RequirementSubjectSymbolDeSerTOP {
  @Override
  protected void serializeTypes(List<SymTypeExpression> types, SysMLRequirementsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "types", types);
  }

  @Override
  protected List<SymTypeExpression> deserializeTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember( "types", symbolJson);
  }
}
