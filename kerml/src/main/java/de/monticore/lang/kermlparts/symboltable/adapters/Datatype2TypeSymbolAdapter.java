package de.monticore.lang.kermlparts.symboltable.adapters;

import de.monticore.lang.kermlelements._symboltable.DatatypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

/**
 * Serializes KerML datatypes with the compatible {@link TypeSymbol} kind,
 * retaining the fields and nested scopes written by the KerML serializer.
 */
public class Datatype2TypeSymbolAdapter extends DatatypeSymbolDeSer {

  @Override
  public String getSerializedKind() {
    return TypeSymbol.class.getName();
  }
}
