package de.monticore.lang.sysmlv2._auxiliary;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;

import java.util.Collections;
import java.util.List;

/**
 * Auxiliary Mills erlauben es die Mills von Super-Sprachen zu verändern. Die "normale" Mill lässt sich mit dem TOP-
 * Mechanismusm nicht mehr überschreiben, da die Mill nicht neu generiert wird. Die Auxiliary Mill schon!
 */
public class BasicSymbolsMillForSysMLv2  extends BasicSymbolsMillForSysMLv2TOP {

  /**
   * Wir erweitern die Liste der Primitiven Typen um ein "nat". Vorgehen kopiert von der
   * {@link de.monticore.symbols.basicsymbols.BasicSymbolsMill}!
   */
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
