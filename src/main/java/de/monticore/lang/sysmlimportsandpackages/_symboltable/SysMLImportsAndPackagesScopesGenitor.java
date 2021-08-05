/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;

public class SysMLImportsAndPackagesScopesGenitor extends SysMLImportsAndPackagesScopesGenitorTOP {

  /** Set name of scope to name of package */
  @Override
  protected void initScopeHP1(ISysMLImportsAndPackagesScope scope) {
    if(scope.isPresentAstNode() && scope.getAstNode() instanceof ASTSysMLPackage) {
      scope.setName(((ASTSysMLPackage) scope.getAstNode()).getName());
    }
  }
}
