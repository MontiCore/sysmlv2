/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ASTImportUnitStd extends ASTImportUnitStdTOP{
  List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();
  List<CoCoStatus> warnings = new ArrayList<>();
  List<SysMLTypeSymbol> transitiveImports = new ArrayList<>();
  boolean init  = false;

  public void setResolvedTypes(List<SysMLTypeSymbol> resolvedTypes) {
    this.resolvedTypes = resolvedTypes;
    init = true;
  }

  public boolean isInit() {
    return init;
  }

  public void setWarnings(List<CoCoStatus> warnings) {
    if(this.warnings.size()!=0){
      this.warnings.addAll(warnings);
    }else{
      this.warnings = warnings;
    }
  }

  public List<SysMLTypeSymbol> getResolvedTypes() {
    return resolvedTypes;
  }

  public List<CoCoStatus> getWarnings() {
    if(!init){
      Log.error("Internal error in " + this.getClass().getName()
          + ". Trying to getWarnings without resolving Types first.");
    }
    return warnings;
  }

  public List<SysMLTypeSymbol> getTransitiveImports() {
    return transitiveImports;
  }

  public void setTransitiveImports(List<SysMLTypeSymbol> transitiveImports) {
    this.transitiveImports = transitiveImports;
  }
}
