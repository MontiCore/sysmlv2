package de.monticore.lang.kerml;

import de.monticore.lang.kermlelements._symboltable.DatatypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

/**
 * Unsauberer Trick um aus Datatype-Symbolen BasicTypes-Typsymbole zu machen.
 * Die Datatype-Symbole sehen serialisiert _fast_ so aus wie TypeSymbols,
 * lediglich das Symbolkind ist das falsche. Hier wird deswegen
 * getSerializedKind() überschrieben um den "Kind:"-String zu verändern.
 * TODO: Restlos zu Entfernen!
 */
public class DatatypeAsTypeSymbolDeSer extends DatatypeSymbolDeSer {

  @Override
  public String getSerializedKind() {
   return TypeSymbol.class.getName();
  }

}
