package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.se_rwth.commons.Symbol;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLSymbolTableResolver {
  private static SysMLSymbolTableResolver instance;
  private SysMLSymbolTableResolver () {}
  public static SysMLSymbolTableResolver getInstance () {
    if (SysMLSymbolTableResolver.instance == null) {
      SysMLSymbolTableResolver.instance = new SysMLSymbolTableResolver ();
    }
    return SysMLSymbolTableResolver.instance;
  }

  //TODO delete
  /* public List<Symbol>  resolveName(SysMLGlobalScope scope){
    for (:
         ) {

    }
  }

  public List<Symbol> resolveInScope(SysMLGlobalScope scope){
    return scope.resolveBlockMany();
  }*/
}
