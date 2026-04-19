package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SysMLv2ScopesGenitor extends SysMLv2ScopesGenitorTOP {

  /**
   * Hook point for the first phase of the artifact scope initialization.
   * This method is invoked by the generated {@code createFromAST} method
   * before the AST traversal (visit/handle) begins.
   * It is used to populate the {@link ISysMLv2ArtifactScope} with information
   * extracted directly from the root node, such as the package name and
   * import statements.
   * <p> Implementation Note: This implementation maps SysMLv2 import statements
   * to MontiCore {@link ImportStatement} objects. Please note that recursive
   * imports are currently handled and represented as wildcard (star) imports
   * within the symbol table. </p>
   * @param artifactScope the artifact scope currently being initialized.
   */
  @Override
  protected void initArtifactScopeHP1(ISysMLv2ArtifactScope artifactScope) {
    super.initArtifactScopeHP1(artifactScope);

    if(!artifactScope.isPresentAstNode() ||
        !(artifactScope.getAstNode() instanceof ASTSysMLModel)) {
      // We do only initialize scopes created in createFromAST, which do have
      // an ast-node and are build upon a ASTSysMLModel.
      // For other cases we do abort. This line is not expected to be reached.
      Log.info("Initializing artifacts-scope build from non ASTSysMLModel",
               SysMLv2ScopesGenitor.class.getName());
      return;
    }

    var importsList = ((ASTSysMLModel) artifactScope.getAstNode())
        .getSysMLElementList()
        .stream()
        .filter(e -> e instanceof ASTSysMLImportStatement)
        .map(e -> (ASTSysMLImportStatement) e)
        .map(i ->
            new ImportStatement(i.getMCQualifiedName().getQName(),
                          i.isStar() || i.isRecursive()))
        .collect(Collectors.toCollection(ArrayList::new));
    artifactScope.setImportsList(importsList);
  }
}
