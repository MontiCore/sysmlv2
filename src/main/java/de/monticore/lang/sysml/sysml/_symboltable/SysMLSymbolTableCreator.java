package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreator;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLSymbolTableCreator extends de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreatorTOP {
  public SysMLSymbolTableCreator(ISysMLScope enclosingScope) {
    super(enclosingScope);
    //System.out.println("SysMLSymbolTableCreator is called!"); //TODO
  }

  public SysMLSymbolTableCreator(Deque<? extends ISysMLScope> scopeStack) {
    super(scopeStack);
    //System.out.println("SysMLSymbolTableCreator is called!");
  }

  /* Overwritting the generated code by template core.Method*/
  public  void addToScope (de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol symbol) {
    //TODO this is not called
    //System.out.println("Creating symbol for " +symbol.getName());
    if (getCurrentScope().isPresent()) {
      if(symbol.getName().equals("")){
        Log.debug("I do not create a symbol, because the name is empty;" , "SymbolTablerCreator");
        System.out.println("I do not create a symbol, because the name is empty;"  + "SymbolTablerCreator");
        //TODO remove
      }else {
        getCurrentScope().get().add(symbol);
      }
    }
    else {
      Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
    }
  }
}
