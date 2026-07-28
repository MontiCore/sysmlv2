package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class SysMLv2DeSer extends SysMLv2DeSerTOP {
  @Override
  protected void serializeImports(List<ImportStatement> imports,
                                  SysMLv2Symbols2Json s2j) {

  }

  @Override
  protected List<ImportStatement> deserializeImports(JsonObject scopeJson) {
    return null;
  }
}
