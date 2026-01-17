/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.types;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeObscure;

/**
 * Kurzfristige Lösung, damit Symbole wie 'Eps' und 'Tick' keine Fehler werfen
 */
public class SilentObscureType extends SymTypeObscure {

  @Override
  public SymTypeExpression deepClone() {
    return new SilentObscureType();
  }

  @Override
  public TypeSymbol getTypeInfo() {
    return null;
  }

  @Override
  public boolean hasTypeInfo() {
    return false;
  }
}
