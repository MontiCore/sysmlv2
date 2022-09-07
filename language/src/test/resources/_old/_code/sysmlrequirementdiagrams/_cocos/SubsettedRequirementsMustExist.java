package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * CoCo verifies that if a requirement usage subsets other requirement usages, then these subsetted
 * requirement usages must be resolvable.
 */
public class SubsettedRequirementsMustExist implements SysMLRequirementDiagramsASTRequirementUsageCoCo {

  @Override
  public void check(ASTRequirementUsage node) {
    if (node.isPresentSysMLSubsetting()) {
      for (ASTMCQualifiedName field : node.getSysMLSubsetting().getFieldsList()) {
        Optional<RequirementUsageSymbol> subsettingUsage = node.getEnclosingScope().resolveRequirementUsage(
            field.getQName());
        if (!subsettingUsage.isPresent()) {
          Log.error("Subsetted requirement usage " + field.getQName() + " could not be resolved.",
              field.get_SourcePositionStart(), field.get_SourcePositionEnd());
        }
      }
    }
  }
}
