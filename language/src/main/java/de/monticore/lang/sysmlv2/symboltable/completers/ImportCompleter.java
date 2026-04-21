package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis.TESTSymbolWithImports;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLNamespace;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.symboltable.ImportStatement;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ImportCompleter implements SysMLBasisVisitor2,
    SysMLImportsAndPackagesVisitor2 {

  @Override
  public void visit(ASTSysMLPackage node) {
    if(node instanceof ASTSysMLNamespace &&       // check wäre nötig wenn der visit-typ abstrahier wird
        node.isPresentSymbol() &&
        node.getSymbol() instanceof TESTSymbolWithImports) { // check wäre nötig wenn der visit-typ abstrahier wird

      var elementsSysMlElements = node.getSysMLElementList();

      var imports = elementsSysMlElements
          .stream()
          .filter(e -> e instanceof ASTSysMLImportStatement)
          .map(e -> (ASTSysMLImportStatement) e)
          .map(i ->
              new ImportStatement(i.getMCQualifiedName().getQName(),
                  i.isStar() || i.isRecursive()))
          .collect(Collectors.toCollection(ArrayList::new));

      node.getSymbol().setImportsList(imports);
    }
  }

//  @Override
//  public void visit(ASTSysMLElement node) {
//    if(!(node instanceof ASTSysMLNamespace) ||
//        node.getEnclosingScope() == null ||
//
//        !node.getEnclosingScope().isPresentSpanningSymbol() ||
//        !(node.getEnclosingScope().getSpanningSymbol() instanceof NamespaceSymbol)) {
//      return;
//    }
//
//    var elementsSysMlElements = ((ASTSysMLNamespace) node).getSysMLElementList();
//
//    var imports = elementsSysMlElements
//        .stream()
//        .filter(e -> e instanceof ASTSysMLImportStatement)
//        .map(e -> (ASTSysMLImportStatement) e)
//        .map(i ->
//            new ImportStatement(i.getMCQualifiedName().getQName(),
//                i.isStar() || i.isRecursive()))
//        .collect(Collectors.toCollection(ArrayList::new));
//
//    ((NamespaceSymbol) node.getEnclosingScope().getSpanningSymbol())
//        .setImportsList(imports);
//  }
}
