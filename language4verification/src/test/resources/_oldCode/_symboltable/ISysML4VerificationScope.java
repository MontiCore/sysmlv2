package schrott._symboltable;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageBody;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Predicate;

import static de.se_rwth.commons.Joiners.DOT;

public interface ISysML4VerificationScope extends ISysML4VerificationScopeTOP {

  @Override
  default List<PortDefinitionStdSymbol> continueAsPortDefinitionStdSubScope(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<PortDefinitionStdSymbol> predicate) {
    List<PortDefinitionStdSymbol> resultList = new ArrayList<>();
    setPortDefinitionStdSymbolsAlreadyResolved(false);

    if (checkIfContinueAsSubScope(name)) {
      for (String remainingSymbolName : getRemainingNameForResolveDown(name)) {
        resultList.addAll(
            this.resolvePortDefinitionStdDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
      if (this.isPresentName()) {
        String remainingSymbolName = this.getName() + "." + name;
        resultList.addAll(
            this.resolvePortDefinitionStdDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
    }
    return resultList;
  }

  @Override
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdDownMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<PortDefinitionStdSymbol> predicate) {
    // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
    // during the current execution of the resolution algorithm
    if (isPortDefinitionStdSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
    // to prevent resolving cycles caused by cyclic symbol adapters
    setPortDefinitionStdSymbolsAlreadyResolved(true);
    final List<PortDefinitionStdSymbol> resolvedSymbols = this.resolvePortDefinitionStdLocallyMany(
        foundSymbols, name, modifier, predicate);
    foundSymbols = foundSymbols || resolvedSymbols.size() > 0;
    setPortDefinitionStdSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "PortDefinitionStdSymbol"
        + "\") in scope \"" + (isPresentName() ?
        getName() :
        "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

    // If no matching symbols have been found...
    if (resolvedSymbols.isEmpty()) {
      // (1.1) Continue search in sub scopes and ...
      for (ISysML4VerificationScope subScope : getSubScopes()) {
        final List<PortDefinitionStdSymbol> resolvedFromSub = subScope
            .continueAsPortDefinitionStdSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromSub.size() > 0;
        // (1.2) unify results
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }

    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    setPortDefinitionStdSymbolsAlreadyResolved(false);
    return resolvedSymbols;
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
      String name,
      AccessModifier modifier) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolvePortDefinitionStdMany(name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<PortDefinitionStdSymbol> predicate) {
    return this.resolvePortDefinitionStdMany(false, name, pathToSymbol, modifier, predicate);
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
      String name,
      Predicate<PortDefinitionStdSymbol> predicate) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolvePortDefinitionStdMany(false, name, pathToSymbol, AccessModifier.ALL_INCLUSION, predicate);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier) {
    return this.resolvePortDefinitionStdMany(foundSymbols, name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<PortDefinitionStdSymbol> predicate) {
    // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
    // during the current execution of the resolution algorithm
    if (isPortDefinitionStdSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
    // to prevent resolving cycles caused by cyclic symbol adapters
    setPortDefinitionStdSymbolsAlreadyResolved(true);
    final List<PortDefinitionStdSymbol> resolvedSymbols = this.resolvePortDefinitionStdLocallyMany(foundSymbols, name,
        modifier, predicate);
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setPortDefinitionStdSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveMany(\"" + name + "\", \"" + "PortDefinitionStdSymbol"
        + "\") in scope \"" + (isPresentName() ?
        getName() :
        "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

    // Following part was added to resolve parts of the symbol as scopes if the symbol wasn't resolved until this point
    // If no matching symbols have been found...
    if (resolvedSymbols.isEmpty()) {
      // (2) Continue search in sub scopes and ...
      for (ISysML4VerificationScope subScope : getSubScopes()) {
        final List<PortDefinitionStdSymbol> resolvedFromSub = subScope
            .continueAsPortDefinitionStdSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromSub.size() > 0;
        // (3) unify results
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }

    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setBlockSymbolsAlreadyResolved(false);

    // (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
    final List<PortDefinitionStdSymbol> resolvedFromEnclosing = continuePortDefinitionStdWithEnclosingScope(
        foundSymbols, name, pathToSymbol, modifier, predicate);

    // (3) unify results
    resolvedSymbols.addAll(resolvedFromEnclosing);
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

    return resolvedSymbols;
  }

  // The modified resolving methods sometimes find the same symbols multiples times. Thus, we filter for duplicates.
  @Override
  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    Set<T> resolvedMutual = new LinkedHashSet<>(resolved);
    if (resolvedMutual.size() == 1) {
      return Optional.of(resolvedMutual.iterator().next());
    }
    else if (resolvedMutual.size() > 1) {
      throw new ResolvedSeveralEntriesForSymbolException("0xA4095 Found " + resolvedMutual.size()
          + " symbols: " + resolvedMutual.iterator().next().getFullName(),
          resolved);
    }
    return Optional.empty();
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes
   */
  default List<PortDefinitionStdSymbol> continuePortDefinitionStdWithEnclosingScope(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<PortDefinitionStdSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      // add package name in 'pathToSymbol' for symbol resolution later from global scope
      // this was added to support symbol resolution when a namespace is distributed across files
      if (getEnclosingScope().isPresentAstNode() && getEnclosingScope().getAstNode() instanceof ASTPackageBody) {
        pathToSymbol.add(getEnclosingScope().getName());
      }
      return getEnclosingScope().resolvePortDefinitionStdMany(foundSymbols, name, pathToSymbol, modifier, predicate);
    }
    return new ArrayList<>();
  }

  @Override
  default List<BlockSymbol> continueAsBlockSubScope(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<BlockSymbol> predicate) {
    List<BlockSymbol> resultList = new ArrayList<>();
    setBlockSymbolsAlreadyResolved(false);

    if (checkIfContinueAsSubScope(name)) {
      for (String remainingSymbolName : getRemainingNameForResolveDown(name)) {
        resultList.addAll(this.resolveBlockDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
      if (this.isPresentName() && (getNameParts(name).get(0).equals(this.getName()))) {
        String remainingName = DOT.join(getNameParts(name).skip(1));
        resultList.addAll(this.resolveBlockDownMany(foundSymbols, remainingName, modifier, predicate));
      }

      else if (this.isPresentName()) {
        String remainingSymbolName = this.getName() + "." + name;
        resultList.addAll(this.resolveBlockDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
    }
    return resultList;
  }

  /**
   * Method overridden to add resolution of parts of the symbol as scopes if it couldn't be resolved as is
   */
  @Override
  default List<BlockSymbol> resolveBlockMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<BlockSymbol> predicate) {
    // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
    // during the current execution of the resolution algorithm
    if (isBlockSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
    // to prevent resolving cycles caused by cyclic symbol adapters
    setBlockSymbolsAlreadyResolved(true);
    final List<BlockSymbol> resolvedSymbols = this.resolveBlockLocallyMany(foundSymbols, name, modifier, predicate);
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setBlockSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveMany(\"" + name + "\", \"" + "BlockSymbol"
        + "\") in scope \"" + (isPresentName() ?
        getName() :
        "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

    // Following part was added to resolve parts of the symbol as scopes if the symbol wasn't resolved until this point
    // If no matching symbols have been found...
    if (resolvedSymbols.isEmpty()) {
      // (2) Continue search in sub scopes and ...
      for (ISysML4VerificationScope subScope : getSubScopes()) {
        final List<BlockSymbol> resolvedFromSub = subScope
            .continueAsBlockSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromSub.size() > 0;
        // (3) unify results
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setBlockSymbolsAlreadyResolved(false);

    // (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
    final List<BlockSymbol> resolvedFromEnclosing = continueBlockWithEnclosingScope(foundSymbols, name, modifier,
        predicate);

    // (3) unify results
    resolvedSymbols.addAll(resolvedFromEnclosing);
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

    return resolvedSymbols;
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<BlockSymbol> resolveBlockMany(
      String name,
      AccessModifier modifier) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolveBlockMany(name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<BlockSymbol> resolveBlockMany(
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<BlockSymbol> predicate) {
    return this.resolveBlockMany(false, name, pathToSymbol, modifier, predicate);
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<BlockSymbol> resolveBlockMany(
      String name,
      Predicate<BlockSymbol> predicate) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolveBlockMany(false, name, pathToSymbol, AccessModifier.ALL_INCLUSION, predicate);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<BlockSymbol> resolveBlockMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier) {
    return this.resolveBlockMany(foundSymbols, name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<BlockSymbol> resolveBlockMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<BlockSymbol> predicate) {
    // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
    // during the current execution of the resolution algorithm
    if (isBlockSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
    // to prevent resolving cycles caused by cyclic symbol adapters
    setBlockSymbolsAlreadyResolved(true);
    final List<BlockSymbol> resolvedSymbols = this.resolveBlockLocallyMany(foundSymbols, name,
        modifier, predicate);
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setBlockSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveMany(\"" + name + "\", \"" + "BlockSymbol"
        + "\") in scope \"" + (isPresentName() ?
        getName() :
        "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

    // Following part was added to resolve parts of the symbol as scopes if the symbol wasn't resolved until this point
    // If no matching symbols have been found...
    if (resolvedSymbols.isEmpty()) {
      // (1.1) Continue search in sub scopes and ...
      for (ISysML4VerificationScope subScope : getSubScopes()) {
        final List<BlockSymbol> resolvedFromSub = subScope
            .continueAsBlockSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromSub.size() > 0;
        // (1.2) unify results
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }

    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setBlockSymbolsAlreadyResolved(false);

    // (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
    final List<BlockSymbol> resolvedFromEnclosing = continueBlockWithEnclosingScope(
        foundSymbols, name, pathToSymbol, modifier, predicate);

    // (3) unify results
    resolvedSymbols.addAll(resolvedFromEnclosing);
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

    return resolvedSymbols;
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes
   */
  default List<BlockSymbol> continueBlockWithEnclosingScope(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<BlockSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      // add package name in 'pathToSymbol' for symbol resolution later from global scope
      // this was added to support symbol resolution when a namespace is distributed across files
      if (getEnclosingScope().isPresentAstNode() && getEnclosingScope().isPresentName()
          && getEnclosingScope().getAstNode() instanceof ASTPackageBody) {
        pathToSymbol.add(getEnclosingScope().getName());
      }
      return getEnclosingScope().resolveBlockMany(foundSymbols, name, pathToSymbol, modifier, predicate);
    }
    return new ArrayList<>();
  }

  @Override
  default List<StateDefinitionSymbol> continueAsStateDefinitionSubScope(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<StateDefinitionSymbol> predicate) {
    List<StateDefinitionSymbol> resultList = new ArrayList<>();
    setStateDefinitionSymbolsAlreadyResolved(false);

    if (checkIfContinueAsSubScope(name)) {
      for (String remainingSymbolName : getRemainingNameForResolveDown(name)) {
        resultList.addAll(this.resolveStateDefinitionDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
      if (this.isPresentName() && (getNameParts(name).get(0).equals(this.getName()))) {
        String remainingName = DOT.join(getNameParts(name).skip(1));
        resultList.addAll(this.resolveStateDefinitionDownMany(foundSymbols, remainingName, modifier, predicate));
      }

      else if (this.isPresentName()) {
        String remainingSymbolName = this.getName() + "." + name;
        resultList.addAll(this.resolveStateDefinitionDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
      }
    }
    return resultList;
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<StateDefinitionSymbol> resolveStateDefinitionMany(
      String name,
      AccessModifier modifier) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolveStateDefinitionMany(name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<StateDefinitionSymbol> resolveStateDefinitionMany(
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<StateDefinitionSymbol> predicate) {
    return this.resolveStateDefinitionMany(false, name, pathToSymbol, modifier, predicate);
  }

  /**
   * Method overridden to handle shared namespace across files by adding a running
   * list of paths to symbol.
   */
  @Override
  default List<StateDefinitionSymbol> resolveStateDefinitionMany(
      String name,
      Predicate<StateDefinitionSymbol> predicate) {
    List<String> pathToSymbol = new ArrayList<>();
    return this.resolveStateDefinitionMany(false, name, pathToSymbol, AccessModifier.ALL_INCLUSION, predicate);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<StateDefinitionSymbol> resolveStateDefinitionMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier) {
    return this.resolveStateDefinitionMany(foundSymbols, name, pathToSymbol, modifier, x -> true);
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes.
   */
  default List<StateDefinitionSymbol> resolveStateDefinitionMany(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<StateDefinitionSymbol> predicate) {
    // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
    // during the current execution of the resolution algorithm
    if (isStateDefinitionSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
    // to prevent resolving cycles caused by cyclic symbol adapters
    setStateDefinitionSymbolsAlreadyResolved(true);
    final List<StateDefinitionSymbol> resolvedSymbols = this.resolveStateDefinitionLocallyMany(foundSymbols, name,
        modifier, predicate);
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setStateDefinitionSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveMany(\"" + name + "\", \"" + "StateDefinitionSymbol"
        + "\") in scope \"" + (isPresentName() ?
        getName() :
        "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

    // Following part was added to resolve parts of the symbol as scopes if the symbol wasn't resolved until this point
    // If no matching symbols have been found...
    if (resolvedSymbols.isEmpty()) {
      // (1.2) Continue search in sub scopes and ...
      for (ISysML4VerificationScope subScope : getSubScopes()) {
        final List<StateDefinitionSymbol> resolvedFromSub = subScope
            .continueAsStateDefinitionSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromSub.size() > 0;
        // (1.3) unify results
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }

    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
    setStateDefinitionSymbolsAlreadyResolved(false);

    // (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
    final List<StateDefinitionSymbol> resolvedFromEnclosing = continueStateDefinitionWithEnclosingScope(
        foundSymbols, name, pathToSymbol, modifier, predicate);

    // (3) unify results
    resolvedSymbols.addAll(resolvedFromEnclosing);
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

    return resolvedSymbols;
  }

  /**
   * Overloaded method added to maintain a running list of enclosing packages
   * as we continue to search for symbol in the enclosing scopes
   */
  default List<StateDefinitionSymbol> continueStateDefinitionWithEnclosingScope(
      boolean foundSymbols,
      String name,
      List<String> pathToSymbol,
      AccessModifier modifier,
      Predicate<StateDefinitionSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      // add package name in 'pathToSymbol' for symbol resolution later from global scope
      // this was added to support symbol resolution when a namespace is distributed across files
      if (getEnclosingScope().isPresentAstNode() && getEnclosingScope().isPresentName()
          && getEnclosingScope().getAstNode() instanceof ASTPackageBody) {
        pathToSymbol.add(getEnclosingScope().getName());
      }
      return getEnclosingScope().resolveStateDefinitionMany(foundSymbols, name, pathToSymbol, modifier, predicate);
    }
    return new ArrayList<>();
  }
}
