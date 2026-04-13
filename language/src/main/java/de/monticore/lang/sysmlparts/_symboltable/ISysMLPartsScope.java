package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlimportsandpackages._symboltable.SysMLPackageSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import java.util.*;
import java.util.function.Predicate;

public interface ISysMLPartsScope extends ISysMLPartsScopeTOP {

  @Override
  default List<PartDefSymbol> continuePartDefWithEnclosingScope(
    boolean foundSymbols,
    String name,
    AccessModifier modifier,
    Predicate<PartDefSymbol> predicate
  ) {
    final LinkedHashSet<PartDefSymbol> result = new LinkedHashSet<>();

    if (
      checkIfContinueWithEnclosingScope(foundSymbols) &&
      getEnclosingScope() != null)
    {
      Set<String> potentialQualifiedNames = calcQNamesForEnclosingScope(name);

      for (String potentialName : potentialQualifiedNames)
      {
        result.addAll(getEnclosingScope().resolvePartDefMany(
          foundSymbols,
          potentialName,
          modifier,
          predicate
        ));
      }
    }

    return new ArrayList<>(result);
  }

  /**
   * Local version of 'calculateQualifiedNames'.
   * Uses the name of the current Scope as Prefix.
   */
  default Set<String> calcQNamesForEnclosingScope(String name) {
    Set<String> potentialSymbolNames = new LinkedHashSet<>();
    potentialSymbolNames.add(name);

    if (
      this.isPresentSpanningSymbol()
      && this.getSpanningSymbol() instanceof SysMLPackageSymbol
    ) {
      potentialSymbolNames.add(this.getSpanningSymbol().getName() + "." + name);
    }

    //import Statements are not considered in local Scopes

    return potentialSymbolNames;
  }
}
