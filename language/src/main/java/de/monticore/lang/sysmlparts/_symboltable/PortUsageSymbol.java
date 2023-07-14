package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortUsageSymbol extends PortUsageSymbolTOP {

  public PortUsageSymbol(String name) {
    super(name);
  }

  /**
   * Returns the total set of attributes in this port usage and any of its port definitions. This includes:
   * - attribute usages defined ad-hoc in the usage
   * - attributes defined in any specialized port definition (can be multiple)
   * - attributes defined in any subsetted port usages (can be multiple)
   * - and obviously works recursively across specialization chains
   */
  public List<AttributeUsageSymbol> getInnerAttributes() {
    var res = new ArrayList<AttributeUsageSymbol>();

    // 1. Locally defined
    res.addAll(getSpannedScope().getLocalAttributeUsageSymbols());

    // 2. & 3. Any port definition/usage (TODO CoCo, die prüft, dass specializations von port usages immer ports sind)
    // & 4. recursively across superTypes (TODO superTypes in Adapter implementieren)
    res.addAll(getTypesList().stream()
        .flatMap(t -> getInnerAttributes(t.getTypeInfo()).stream())
        .collect(Collectors.toList()));

    return res;
  }

  /**
   * Gets inner attributes of types that span a ISysMLPartsScope scope. Returns empty list otherwise.
   * Works recursively across supertypes, disregarding of the scope type.
   */
  private static List<AttributeUsageSymbol> getInnerAttributes(TypeSymbol type) {
    var res = new ArrayList<AttributeUsageSymbol>();
    if(type.getSpannedScope() != null && type.getSpannedScope() instanceof ISysMLPartsScope) {
      var scope = (ISysMLPartsScope) type.getSpannedScope();
      res.addAll(scope.getLocalAttributeUsageSymbols());
    }
    res.addAll(type.getSuperTypesList().stream()
        .flatMap(t -> getInnerAttributes(t.getTypeInfo()).stream())
        .collect(Collectors.toList()));
    return res;
  }

}
