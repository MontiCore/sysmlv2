package de.monticore.lang.sysmlv2._symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortDefSymbol extends PortDefSymbolTOP {

  public PortDefSymbol(String name) {
    super(name);
  }

  public List<AttributeUsageSymbol> getInputAttributes() {
    var res = new ArrayList<AttributeUsageSymbol>();

    // Lokal
    res.addAll(getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(AttributeUsageSymbol::isIn)
        .collect(Collectors.toList()));

    // Von supertypes
    // TODO

    return res;
  }

  public List<AttributeUsageSymbol> getOutputAttributes() {
    var res = new ArrayList<AttributeUsageSymbol>();

    // Lokal
    res.addAll(getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(AttributeUsageSymbol::isOut)
        .collect(Collectors.toList()));

    // Von supertypes
    // TODO

    return res;
  }

}
