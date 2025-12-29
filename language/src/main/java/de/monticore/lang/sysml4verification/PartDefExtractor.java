package de.monticore.lang.sysml4verification;

import de.monticore.lang.sysml4verification._ast.ASTPartDef;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysml4verification._visitor.SysML4VerificationVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Extracts PartDefs as ComponentTypeSymbols and adds them to a new scope.
 */
public class PartDefExtractor implements SysML4VerificationVisitor2 {
  private ISysML4VerificationScope artifact;

  public PartDefExtractor(ISysML4VerificationScope artifact) {
    this.artifact = artifact;
  }

  @Override
  public void visit(ASTPartDef node) {
    if (node.getEnclosingScope() instanceof ISysML4VerificationScope) {
      var scope = node.getEnclosingScope();
      var component = scope.resolveComponentType(node.getName());
      if (component.isPresent()) {
        artifact.add(component.get());
      }
      else {
        var msg = String.format("PartDef %s could not be resolved as "
            + "ComponentType.", node.getName());
        Log.warn(msg, node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
    }
  }
}
