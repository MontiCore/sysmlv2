package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLGlobalScope extends SysMLGlobalScopeTOP {

  public SysMLGlobalScope getRealThis() {
    return this;
  }

  /* generated by template core.Constructor*/
  public SysMLGlobalScope(de.monticore.io.paths.ModelPath modelPath, SysMLLanguage language) {
    super(modelPath, language);
  }

  //It is not possible to load a model by a name from SysMLType, because names and filenames are not dependent in
  // SysML
  public void loadModelsForSysMLType(String name) {
  }

  public void loadModelsForType(String name) {
  }

  public void loadModelsForField(String name) {
  }

  public void loadModelsForMethod(String name) {
  }
}
