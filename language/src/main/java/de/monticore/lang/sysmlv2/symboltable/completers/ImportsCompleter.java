package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Visitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.visitor.IVisitor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO complete public imports
// currently only packages and defs as namespaces are supported
public class ImportsCompleter implements SysMLImportsAndPackagesVisitor2 {
  @Override
  public void visit(ASTSysMLImportStatement node) {
    if (node.isPublic()) {
      node.getEnclosingScope().addSysMLImports(node);
    }
    /*
    if (node.isRecursive()) {
      var packagee = node.getEnclosingScope().resolveSysMLPackage(node.getQName());
      var def = ((ISysMLv2Scope)node.getEnclosingScope()).resolvePartDef(node.getQName());
      IScope targetScope = null;
      if (packagee.isPresent()) {
        // TODO here there could be both present
        targetScope = packagee.get().getSpannedScope();
      } else if (def.isPresent()){
        targetScope = def.get().getSpannedScope();
      }

      if (targetScope != null) {
        SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
        var visitor = new RecursiveCompleterHelper();
        traverser.add4IVisitor(visitor);
        targetScope.accept(traverser);

        var symbols = visitor.getCollectedSymbols();
        var statements = symbols
            .stream()
            .map(sym ->
                SysMLNames.getQualifiedName(
                    SysMLNames.getRelativeFromFqn(
                        node.getMCQualifiedName().getPartsList(),
                        node.getEnclosingScope().getNameParts(sym.getFullName()).toList()
                    )
                ))
            .toList();

        // TODO hacky cuz there are not sets generated in MC
        var importStatements = new ArrayList<>(node.getEnclosingScope().getImportsList());
        importStatements.addAll(statements.stream().map(statement ->
            new ImportStatement(statement, false)).toList());

        record StatementKey(String statement, boolean isStar) {}

        node.getEnclosingScope().setImportsList(importStatements
            .stream()
            .filter(distinctByKey(p -> new StatementKey(p.getStatement(), p.isStar())))
            .toList());

      }
    }

     */
  }

  private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  public static class RecursiveCompleterHelper implements IVisitor {
    // TODO here you could only traverse IScopeSpanningSymbols and thus map to * imports
    protected List<ISymbol> collectedSymbols = new ArrayList<>();
    @Override
    public void visit(ISymbol symbol) {
      getCollectedSymbols().add(symbol);
    }

    public List<ISymbol> getCollectedSymbols() {
      return collectedSymbols;
    }
  }
}
