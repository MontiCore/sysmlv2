/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.io.paths.ModelPath;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLGlobalScope extends SysMLGlobalScopeTOP {
  
  public SysMLGlobalScope()  {
    super();
  }

  public SysMLGlobalScope getRealThis() {
    return this;
  }

  public SysMLGlobalScope(ModelPath modelPath, String fileExt) {
    super(modelPath, fileExt);
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
