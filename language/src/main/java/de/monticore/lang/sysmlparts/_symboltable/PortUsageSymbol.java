package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortUsageSymbol extends PortUsageSymbolTOP {

  public PortUsageSymbol(String name) {
    super(name);
  }

  /**
   * Returns the total set of attributes in this port usage and any of its non-confugated port definitions. This
   * includes:
   * - attribute usages defined ad-hoc in the usage
   * - attributes defined in any specialized port definition (can be multiple)
   * - attributes defined in any subsetted port usages (can be multiple)
   * - and obviously works recursively across specialization chains
   */
  public List<AttributeUsageSymbol> getInputAttributes() {
    var res = new ArrayList<AttributeUsageSymbol>();

    // 1. Locally defined
    res.addAll(getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(AttributeUsageSymbol::isIn)
        .collect(Collectors.toList()));

    // TODO CoCo, die prüft, dass specializations von port usages immer ports sind
    // TODO Direkt auf PortDef's zugreifen zu können wäre schöner, allerdings müsste man dazu den SysML-
    //  Standard nochmal lesen: Müssen Super-Typen con PortUsages immer PortDefs sein?

    // 2. Recursively across superTypes

    // Inputs der nicht-conjugated SuperTypes
    res.addAll(getTypesList().stream()
        .map(exp -> { // TODO Geht das besser?
          if (exp.getTypeInfo() instanceof TypeSymbolSurrogate && ((TypeSymbolSurrogate) exp.getTypeInfo()).checkLazyLoadDelegate()) {
            return ((TypeSymbolSurrogate) exp.getTypeInfo()).lazyLoadDelegate();
          } else {
            return exp.getTypeInfo();
          }
        })
        .filter(t -> t instanceof PortDef2TypeSymbolAdapter)
        .map(t -> ((PortDef2TypeSymbolAdapter)t).getAdaptee())
        .flatMap(p -> p.getInputAttributes().stream())
        .collect(Collectors.toList())
    );

    // Outputs der conjugated SuperTypes
    res.addAll(getConjugatedTypesList().stream()
        .map(exp -> { // TODO Geht das besser?
          if (exp.getTypeInfo() instanceof TypeSymbolSurrogate && ((TypeSymbolSurrogate) exp.getTypeInfo()).checkLazyLoadDelegate()) {
            return ((TypeSymbolSurrogate) exp.getTypeInfo()).lazyLoadDelegate();
          } else {
            return exp.getTypeInfo();
          }
        })
        .filter(t -> t instanceof PortDef2TypeSymbolAdapter)
        .map(t -> ((PortDef2TypeSymbolAdapter)t).getAdaptee())
        .flatMap(p -> p.getOutputAttributes().stream())
        .collect(Collectors.toList())
    );

    return res;
  }

  public List<AttributeUsageSymbol> getOutputAttributes() {
    var res = new ArrayList<AttributeUsageSymbol>();

    // 1. Locally defined
    res.addAll(getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(AttributeUsageSymbol::isOut)
        .collect(Collectors.toList()));

    // 2. Recursively across superTypes

    // Outputs der nicht-conjugated SuperTypes
    res.addAll(getTypesList().stream()
        .map(exp -> { // TODO Geht das besser?
          if (exp.getTypeInfo() instanceof TypeSymbolSurrogate && ((TypeSymbolSurrogate) exp.getTypeInfo()).checkLazyLoadDelegate()) {
            return ((TypeSymbolSurrogate) exp.getTypeInfo()).lazyLoadDelegate();
          } else {
            return exp.getTypeInfo();
          }
        })
        .filter(t -> t instanceof PortDef2TypeSymbolAdapter)
        .map(t -> ((PortDef2TypeSymbolAdapter)t).getAdaptee())
        .flatMap(p -> p.getOutputAttributes().stream())
        .collect(Collectors.toList())
    );

    // Inputs der conjugated SuperTypes
    res.addAll(getConjugatedTypesList().stream()
        .map(exp -> { // TODO Geht das besser?
          if (exp.getTypeInfo() instanceof TypeSymbolSurrogate && ((TypeSymbolSurrogate) exp.getTypeInfo()).checkLazyLoadDelegate()) {
            return ((TypeSymbolSurrogate) exp.getTypeInfo()).lazyLoadDelegate();
          } else {
            return exp.getTypeInfo();
          }
        })
        .filter(t -> t instanceof PortDef2TypeSymbolAdapter)
        .map(t -> ((PortDef2TypeSymbolAdapter)t).getAdaptee())
        .flatMap(p -> p.getInputAttributes().stream())
        .collect(Collectors.toList())
    );

    return res;
  }

}
