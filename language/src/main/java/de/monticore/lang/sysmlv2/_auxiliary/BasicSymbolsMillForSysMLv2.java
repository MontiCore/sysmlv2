package de.monticore.lang.sysmlv2._auxiliary;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;

import java.util.Collections;
import java.util.List;

public class BasicSymbolsMillForSysMLv2  extends BasicSymbolsMillForSysMLv2TOP {
  private static final List<String> SYSML2_PRIMITIVE_LIST = Collections.unmodifiableList(Lists.newArrayList("nat"));

  /**
   * Seit MontiCore 7.5 können der BasicSymbolsMill keine neuen Primitiven mehr hinzugefügt werden.
   * Stattdessen kann an dieser Stelle die sprach-spezifische Mill genutzt werden um die Primitiven anzulegen.
   */
  @Override
  public void _initializePrimitives() {
    IBasicSymbolsGlobalScope gs = globalScope();
    for (var primitive : SYSML2_PRIMITIVE_LIST) {
      gs.add(this.createPrimitive(primitive));
    }
    super._initializePrimitives();
  }
}
