package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlimportsandpackages.SysMLImportsAndPackagesMill;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.lang.sysmlimportsandpackages._symboltable.symboltable.NamesUtil.getRelativeFromFqn;

/*
 * In SysML, namespaces live inside SysML models (keyword "package") and
 * there can be multiple namespaces in a single model. This is sometimes
 * referred to as "first class support" of namespaces. In Java-like
 * programming languages, the namespace of an artifact is handled implicitly
 * through the file system path (in conjunction with a package declaration
 * for easier inside-out-resolving). Blocks, methods, or classes are not
 * considered full namespaces (resolving "bar" from within a class "Foo"
 * does not yield the potential qualified name "Foo.bar" at the global scope).
 * MontiCore's default resolve-mechanism is built to behave Java-like, i.e.,
 * it assumes that namespaces exist only at the file level and only package
 * declarations matter for the calculation of potential names. Therefore, the
 * logic of looking for all potential qualified names is only executed when
 * leaving the artifact scope and does not account for any scope names passed
 * on the way up.
 * <br>
 * This override changes this. It explicitly adds one new potential name to
 * the list of potential names every time a package is passed while continuing
 * with the enclosing scope. Assume we look for "bar", we pass "package Foo",
 * then the list of potential names we are resolving for is now
 * ["bar", "Foo.bar"].
 * <br>
 * <b>Notice</b>: SysML comes with a large number of keywords
 * (e.g., occurrence, item, attribute, part) that have no or very little
 * meaning wrt. to symbol resolution. In MontiCore, we already established the
 * basic set of symbols (aptly named "BasicSymbols"), namely Types, Variables,
 * and Functions. To avoid re-implementing resolving functionality for all
 * keywords, we use symbol adapters from SysML definitions to MontiCore types,
 * SysML usages to MontiCore variables, and SysML constraints (including
 * calc defs) to MontiCore functions. This method here handles resolving of
 * MontiCore types, i.e., SysML definitions.
 */
public interface ISysMLImportsAndPackagesScope extends ISysMLImportsAndPackagesScopeTOP {
  /**
   * This method is essentially copied from artifact scopes. See explanation
   * on continueTypeWithEnclosingScope(4): MontiCore's symbol resolution is
   * Java-like out-of-the-box and needs to be extended for SysMLv2's usage
   * of packages (namespaces) as proper modeling elements.
   * Also, the Scopes-Included Imports are used for potential name qualification.
   * Therefore: 1. Matching, direct Imports are taken as FQNs
   *            2. Star and recursive Imports do try to resolve the import
   *               where the wildcard is replaced by the resolved Symbols name.
   *               Therefore, recursive imports are only supported as star-imports.
   */
  default Set<String> calcQNamesForEnclosingScope(String name,
                                                  List<ASTSysMLImportStatement> importStatements) {
    Set<String> potentialNamespaces = new LinkedHashSet<>();

    // if name is already qualified, no further (potential) names exist by imports
    // qualify names based on the import statements of enclosing scope
    // 1. qualify star imports by replacing the start with symbolname
    // 2. qualify direct imports when the name matches
    findAllNamespacesForImports(name, importStatements, potentialNamespaces);
    var res = potentialNamespaces.stream().map(namespace -> namespace + "." + name).collect(
        Collectors.toSet());
    res.add(name);

    return res;
  }

  public default void findAllNamespacesForImports(String name, List<ASTSysMLImportStatement> importStatements, Set<String> namespaces) {
    for (var importStatement : importStatements) {
      // in sysml it is valid to resolve B::C somewhere where A::B was imported
      List<String> partsList = importStatement.getMCQualifiedName().getPartsList();
      List<String> nameList = Splitters.DOT.splitToList(name);
      if (partsList.get(partsList.size() - 1).equals(nameList.get(0)) && !importStatement.isStar()) {
        // in NonStar Recursive imports you also can address the statement itself
        namespaces.add(
            Names.constructQualifiedName(partsList.subList(0, partsList.size() - 1))); // import A; What is that you can find S in there (so go after its orig fqn)
      }
      if (importStatement.isRecursive()) {
        var traverser = SysMLImportsAndPackagesMill.inheritanceTraverser();
        traverser.add4SysMLImportsAndPackages( new SysMLImportsAndPackagesVisitor2() {
          @Override
          public void visit(ISysMLImportsAndPackagesScope scope) {
            // only look in named scopes
            if (scope.isPresentName() && scope.isPresentSpanningSymbol()) {
              namespaces.add(
                  getRelativeFromFqn(importStatement.getQName(),
                      scope.getSpanningSymbol().getFullName()));
              findAllNamespacesForImports(name, scope.getPublicImportsList(), namespaces);
            }
          }
        });
        getEnclosingScope().resolveSysMLNamespace(importStatement.getQName())
            .ifPresent(sysMLTypeSymbol -> sysMLTypeSymbol.getSpannedScope().accept(
            traverser));
      }
      if (importStatement.isStar() && !importStatement.isRecursive()) {
        namespaces.add(importStatement.getQName());
        getEnclosingScope().resolveSysMLNamespace(importStatement.getQName())
            .ifPresent(sysMLNamespaceSymbol -> findAllNamespacesForImports(name,
                ((ISysMLImportsAndPackagesScope) sysMLNamespaceSymbol.getSpannedScope()).getPublicImportsList(),
                namespaces));
      }
    }
  }

  default public LinkedList<ASTSysMLImportStatement> getImportStatementsInCurrentScope() {
    var importStatements = new LinkedList<ASTSysMLImportStatement>();
    if(isPresentAstNode()) {
      // TODO this only finds the imports in enclosing. Why visitor?
      var visitor = new SysMLImportsAndPackagesVisitor2() {
        @Override
        public void visit(ASTSysMLImportStatement node) {
          if (ISysMLImportsAndPackagesScope.this.equals(node.getEnclosingScope())) {
            importStatements.add(node);
          }
        }
      };
      var traverser = SysMLImportsAndPackagesMill.inheritanceTraverser();
      traverser.add4SysMLImportsAndPackages(visitor);
      getAstNode().accept(traverser);
    }
    return importStatements;
  }
}
