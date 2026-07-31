package de.monticore.lang.sysmlv2._symboltable;

import java.util.Optional;

public class SysMLv2Scope extends SysMLv2ScopeTOP {
  public SysMLv2Scope () {
    super();
  }
  public SysMLv2Scope (boolean shadowing) {
    super(shadowing);
  }
  public SysMLv2Scope (de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope enclosingScope) {
    this(enclosingScope, false);
  }
  public SysMLv2Scope (de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope enclosingScope,boolean shadowing) {
    super(enclosingScope, shadowing);
  }
  @Override
  public String toString() {
    return getName();
  }
}
