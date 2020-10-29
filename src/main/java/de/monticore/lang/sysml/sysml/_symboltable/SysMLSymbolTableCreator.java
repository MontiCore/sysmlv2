package de.monticore.lang.sysml.sysml._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Deque;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLSymbolTableCreator extends de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreatorTOP {
  public SysMLSymbolTableCreator(ISysMLScope enclosingScope) {
    super(enclosingScope);
  }

  public SysMLSymbolTableCreator(Deque<? extends ISysMLScope> scopeStack) {
    super(scopeStack);
  }

  /* Overwritting the generated code by template core.Method*/
  public  void addToScope (de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol symbol) {
    if (getCurrentScope().isPresent()) {
      if(symbol.getName().equals("")){
        Log.debug("I do not create a symbol, because the name is empty;" , "SymbolTablerCreator");
      }else {
        getCurrentScope().get().add(symbol);
      }
    }
    else {
      Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
    }
  }
}
