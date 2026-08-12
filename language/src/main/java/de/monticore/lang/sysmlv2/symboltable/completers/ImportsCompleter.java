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

public class ImportsCompleter implements SysMLImportsAndPackagesVisitor2 {
  @Override
  public void visit(ASTSysMLImportStatement node) {
    if (node.isPublic()) {
      node.getEnclosingScope().addPublicImports(node);
    }
  }
}
