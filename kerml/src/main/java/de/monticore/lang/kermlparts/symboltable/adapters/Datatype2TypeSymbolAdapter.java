package de.monticore.lang.kermlparts.symboltable.adapters;

import de.monticore.lang.kermlelements._symboltable.DatatypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;


public class Datatype2TypeSymbolAdapter extends DatatypeSymbolDeSer {

  @Override
  public String getSerializedKind() {
    return TypeSymbol.class.getName();
  }
}
