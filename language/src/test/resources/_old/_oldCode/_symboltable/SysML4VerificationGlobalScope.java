package schrott._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SysML4VerificationGlobalScope extends SysML4VerificationGlobalScopeTOP {

  @Override
  public boolean isPresentName() {
    return false;
  }

  @Override
  public String getName() {
    Log.error("0xA7003x96605 get for Name can't return a value. Attribute is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  @Override
  public SysML4VerificationGlobalScope getRealThis() {
    return null;
  }

  // helper method that returns possible paths to the symbol when resolving from the global scope
  // mutates the given parameter by removing the last element in the list
  private List<String> getCandidatePaths(String name, List<String> pathToSymbol) {
    ArrayList<String> candidates = new ArrayList<>();
    // remove the top most package from the list
    pathToSymbol.remove(pathToSymbol.size() - 1);
    Collections.reverse(pathToSymbol);

    // get possibly optional part of the path
    String optional = String.join(".", pathToSymbol);

    // strip 'path in name' from optional part
    String[] nameParts = name.split("\\.");
    if (nameParts.length > 1) {
      StringBuilder prefix = new StringBuilder(nameParts[0]);
      for (int i = 1; i < nameParts.length - 1; ++i) {
        prefix.append(".").append(nameParts[i]);
      }
      if (optional.endsWith(prefix.toString())) {
        if (optional.equals(prefix.toString())) {
          optional = "";
        }
        else {
          optional = optional.substring(0, optional.length() - prefix.length() - 2);
        }
      }
    }

    for (String optionalPart : optional.split("\\.")) {
      if (candidates.size() == 0) {
        candidates.add(optionalPart);
      }
      else {
        candidates.add(candidates.get(candidates.size() - 1) + "." + optionalPart);
      }
    }
    return candidates;
  }

  /**
   * Helper method that joins the string list with '.' delimiter after removing empty strings from it
   *
   * @param parts list of strings
   * @return joined string
   */
  private String getJoinedPath(ArrayList<String> parts) {
    parts.removeAll(Collections.singletonList(""));
    return String.join(".", parts);
  }

  /**
   *  The method primarily makes use of 'pathToSymbol' parameter to search for symbol.
   *  The search for symbol from global scope is a special case where potentially multiple artifact
   *  scopes can contain the same packages, i.e. essentially a package is distributed across files.
   *  In this case, searching for a symbol in such a package require us to look for all valid possible FQNs for
   *  that symbol.
   *  Search is done in reverse order:
   *      e.g. if full path is example.example2.example3.testsymbol and example is top-level package, then
   *      candidates FQNs are:
   *      1. example.example2.example3.testsymbol
   *      2. example.example2.testsymbol
   *      3. example.testsymbol
   */
  @Override
  public List<PortDefinitionStdSymbol> resolvePortDefinitionStdMany(
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

    String topMostPackage = "";
    List<String> candidatePaths = new ArrayList<>();
    if (pathToSymbol.size() > 0) {
      topMostPackage = pathToSymbol.get(pathToSymbol.size() - 1);
      candidatePaths = this.getCandidatePaths(name, pathToSymbol);
    }

    /*
     We search in reverse because symbols in inner packages would shadow those in the encapsulating ones and hence,
     they have higher precedence.
    */
    for (int i = candidatePaths.size() - 1; i >= 0; --i) {
      List<PortDefinitionStdSymbol> resolvedSymbols = resolvePortDefinitionStdMany(
          foundSymbols,
          this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, candidatePaths.get(i), name))),
          modifier,
          predicate);

      if (resolvedSymbols.size() > 0) {
        return resolvedSymbols;
      }
    }

    // if no candidate paths yielded in symbol resolution, then try only with concatenating the top-most package
    List<PortDefinitionStdSymbol> resolvedSymbols = resolvePortDefinitionStdMany(
        foundSymbols,
        this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, name))),
        modifier,
        predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    // if symbol concatenated with package didn't resolve, then try only with the given name
    resolvedSymbols = resolvePortDefinitionStdMany(foundSymbols, name, modifier, predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    return new ArrayList<>();
  }

  /**
   *  The method primarily makes use of 'pathToSymbol' parameter to search for symbol.
   *  The search for symbol from global scope is a special case where potentially multiple artifact
   *  scopes can contain the same packages, i.e. essentially a package is distributed across files.
   *  In this case, searching for a symbol in such a package require us to look for all valid possible FQNs for
   *  that symbol.
   *  Search is done in reverse order:
   *      e.g. if full path is example.example2.example3.testsymbol and example is top-level package, then
   *      candidates FQNs are:
   *      1. example.example2.example3.testsymbol
   *      2. example.example2.testsymbol
   *      3. example.testsymbol
   */
  @Override
  public List<BlockSymbol> resolveBlockMany(
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

    String topMostPackage = "";
    List<String> candidatePaths = new ArrayList<>();
    if (pathToSymbol.size() > 0) {
      topMostPackage = pathToSymbol.get(pathToSymbol.size() - 1);
      candidatePaths = this.getCandidatePaths(name, pathToSymbol);
    }

    /* search in reverse order
     e.g. if full path is example.example2.example3.testsymbol and example is top-level package, then
     candidates are:
     1. example.example2.example3.testsymbol
     2. example.example2.testsymbol
     3. example.testsymbol

     We search in reverse because symbols in inner packages would shadow those in the encapsulating ones and hence,
     they have higher precedence.
    */
    for (int i = candidatePaths.size() - 1; i >= 0; --i) {
      List<BlockSymbol> resolvedSymbols = resolveBlockMany(
          foundSymbols,
          this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, candidatePaths.get(i), name))),
          modifier,
          predicate);

      if (resolvedSymbols.size() > 0) {
        return resolvedSymbols;
      }
    }

    // if no candidate paths yielded in symbol resolution, then try only with concatenating the top-most package
    List<BlockSymbol> resolvedSymbols = resolveBlockMany(
        foundSymbols,
        this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, name))),
        modifier,
        predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    // if symbol concatenated with package didn't resolve, then try only with the given name
    resolvedSymbols = resolveBlockMany(foundSymbols, name, modifier, predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    return new ArrayList<>();
  }

  /**
   *  The method primarily makes use of 'pathToSymbol' parameter to search for symbol.
   *  The search for symbol from global scope is a special case where potentially multiple artifact
   *  scopes can contain the same packages, i.e. essentially a package is distributed across files.
   *  In this case, searching for a symbol in such a package require us to look for all valid possible FQNs for
   *  that symbol.
   *  Search is done in reverse order:
   *      e.g. if full path is example.example2.example3.testsymbol and example is top-level package, then
   *      candidates FQNs are:
   *      1. example.example2.example3.testsymbol
   *      2. example.example2.testsymbol
   *      3. example.testsymbol
   */
  @Override
  public List<StateDefinitionSymbol> resolveStateDefinitionMany(
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

    String topMostPackage = "";
    List<String> candidatePaths = new ArrayList<>();
    if (pathToSymbol.size() > 0) {
      topMostPackage = pathToSymbol.get(pathToSymbol.size() - 1);
      candidatePaths = this.getCandidatePaths(name, pathToSymbol);
    }

    /* search in reverse order
     e.g. if full path is example.example2.example3.testsymbol and example is top-level package, then
     candidates are:
     1. example.example2.example3.testsymbol
     2. example.example2.testsymbol
     3. example.testsymbol

     We search in reverse because symbols in inner packages would shadow those in the encapsulating ones and hence,
     they have higher precedence.
    */
    for (int i = candidatePaths.size() - 1; i >= 0; --i) {
      List<StateDefinitionSymbol> resolvedSymbols = resolveStateDefinitionMany(
          foundSymbols,
          this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, candidatePaths.get(i), name))),
          modifier,
          predicate);

      if (resolvedSymbols.size() > 0) {
        return resolvedSymbols;
      }
    }

    // if no candidate paths yielded in symbol resolution, then try only with concatenating the top-most package
    List<StateDefinitionSymbol> resolvedSymbols = resolveStateDefinitionMany(
        foundSymbols,
        this.getJoinedPath(new ArrayList<>(Arrays.asList(topMostPackage, name))),
        modifier,
        predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    // if symbol concatenated with package didn't resolve, then try only with the given name
    resolvedSymbols = resolveStateDefinitionMany(foundSymbols, name, modifier, predicate);
    if (resolvedSymbols.size() > 0) {
      return resolvedSymbols;
    }

    return new ArrayList<>();
  }
}
