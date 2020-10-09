package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreator;

import java.util.Deque;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLSymbolTableCreator extends de.monticore.lang.sysml.sysml._symboltable.SysMLSymbolTableCreatorTOP {
  public SysMLSymbolTableCreator(ISysMLScope enclosingScope) {
    super(enclosingScope);
    // System.out.println("SysMLSymbolTableCreator is called!");
  }

  public SysMLSymbolTableCreator(Deque<? extends ISysMLScope> scopeStack) {
    super(scopeStack);
    // System.out.println("SysMLSymbolTableCreator is called!");
  }
}
