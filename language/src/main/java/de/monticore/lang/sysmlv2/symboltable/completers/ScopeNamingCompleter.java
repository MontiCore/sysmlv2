/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Visitor2;
import de.se_rwth.commons.logging.Log;

import java.util.UUID;

/**
 * Sets the name of the artifact scope. This enables cross-artifact resolution. SysML v2 seems to assume that the
 * filename and top-level element (typicallya package) share the same name. There are not "packages" in the MontiCore/
 * Java-sense. Packages in SysML are first-class citizens, i.e., model elements instead of model properties.
 */
public class ScopeNamingCompleter implements SysMLv2Visitor2 {

  @Override
  public void visit(ISysMLv2ArtifactScope scope) {
    if(!scope.isPresentAstNode() || !(scope.getAstNode() instanceof ASTSysMLModel)) {
      Log.debug("The AST was Unexpectedly missing or had the wrong type.", getClass().getName());
    }
    else {
      ASTSysMLModel ast = (ASTSysMLModel) scope.getAstNode();
      if(ast.sizeSysMLElements() == 1) {
        ASTSysMLElement topLevelElement = ast.getSysMLElement(0);
        // TODO Kann man irgendwie generisch (oder mit dem bösen R-Wort, das auf "eflection" endet) den Namen finden?
        if(topLevelElement instanceof ASTSysMLPackage) {
          scope.setName(((ASTSysMLPackage) topLevelElement).getName());
          return;
        }
      }
    }
    // Make it unique but unguessable
    scope.setName("AnonymousArtifact_" + UUID.randomUUID());
  }
}
