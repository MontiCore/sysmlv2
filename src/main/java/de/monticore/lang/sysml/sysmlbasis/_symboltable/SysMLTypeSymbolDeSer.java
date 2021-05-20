package de.monticore.lang.sysml.sysmlbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class SysMLTypeSymbolDeSer extends SysMLTypeSymbolDeSerTOP {

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, SysMLBasisSymbols2Json s2j) {

  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return new ArrayList<>();
  }
}
