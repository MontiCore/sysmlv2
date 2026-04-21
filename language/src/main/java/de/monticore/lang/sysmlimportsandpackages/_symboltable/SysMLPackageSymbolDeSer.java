package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class SysMLPackageSymbolDeSer extends SysMLPackageSymbolDeSerTOP {
  @Override
  protected void serializeImports(List<ImportStatement> imports,
                                  SysMLImportsAndPackagesSymbols2Json s2j) {

  }

  @Override
  protected List<ImportStatement> deserializeImports(JsonObject symbolJson) {
    return List.of();
  }
}
