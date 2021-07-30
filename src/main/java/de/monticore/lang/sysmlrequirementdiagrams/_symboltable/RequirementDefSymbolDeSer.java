package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class RequirementDefSymbolDeSer extends RequirementDefSymbolDeSerTOP {

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, SysMLRequirementDiagramsSymbols2Json s2j) {

  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return new ArrayList<>();
  }
}
