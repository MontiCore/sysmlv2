package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class AttributeDefSymbolDeSer extends AttributeDefSymbolDeSerTOP {
  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, SysMLBlockDiagramsSymbols2Json s2j) {

  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return new ArrayList<>();
  }
}
