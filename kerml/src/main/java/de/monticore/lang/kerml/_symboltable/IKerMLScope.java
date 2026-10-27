package de.monticore.lang.kerml._symboltable;

import de.monticore.lang.kerml.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IKerMLScope extends IKerMLScopeTOP {

  @Override
  default List<TypeSymbol> resolveAdaptedTypeLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<TypeSymbol> predicate
  ) {
    var adapted = new ArrayList<TypeSymbol>();

    // Datatype zu Types
    var datatype = resolveDatatypeLocally(name);
    if (datatype.isPresent()) {
      adapted.add(new Datatype2TypeSymbolAdapter(datatype.get()));
    }

    return adapted;
  }

}
