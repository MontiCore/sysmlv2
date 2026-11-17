/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml;

import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import java.util.ArrayList;
import java.util.List;

public class DatatypeCollector implements KerMLElementsVisitor2 {

  private final List<DatatypeSymbol> datatypes = new ArrayList<>();

  @Override
  public void visit(DatatypeSymbol symbol) {
    datatypes.add(symbol);
  }

  public List<DatatypeSymbol> getDatatypes() {
    return datatypes;
  }
}
