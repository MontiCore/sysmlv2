package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * CoCo verifies that if a requirement has feature typings, then these types must also be resolvable.
 */
public class RequirementDefinitionMustExist implements SysMLRequirementDiagramsASTRequirementUsageCoCo {

  @Override
  public void check(ASTRequirementUsage node) {
    if (node.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) node.getMCType();
      Optional<RequirementDefSymbol> symbol = node.getEnclosingScope()
          .resolveRequirementDef(usageType.getMCQualifiedName().toString());

      if (!symbol.isPresent()) {
        Log.error("Requirement usage '" + node.getName() + "' is defined by requirement definition '" +
                usageType.getMCQualifiedName().toString() + "', but the latter could not be resolved. "
                + "An import may be missing.",
            usageType.get_SourcePositionStart(), usageType.get_SourcePositionEnd());
      }
    }
  }
}
