package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

/**
 * Validate requirement parameters for a requirement usage using the following rules:
 * 1. If there are no generalizations, then do nothing.
 * <p>
 * 2. If there is a single generalization, then it is not necessary that all parameters
 * of the generalization are redefined. If not all parameters are redefined, then the rest
 * of the parameters are inherited. (NOTE: redefined parameters are not inherited!)
 * If requirement wants to declare new parameters, it must first redefine all parameters
 * of the generalization (including the inherited parameters), in order (inherited parameters
 * will come last in the order).
 * <p>
 * 3. If there are multiple generalizations, then it is necessary that all the parameters
 * of all the generalizations are redefined (including the inherited parameters), in order (inherited parameters
 * will come last in the order).
 * Then, the requirement can optionally declare new parameters.
 */
public class SpecializedReqUsageRedefinesInheritedParams
    implements SysMLRequirementDiagramsASTRequirementUsageCoCo {

  @Override
  public void check(ASTRequirementUsage node) {
    // Validate that in case of multiple owned generalizations, all parameters are redefined.
    if((node.isPresentMCType() && node.isPresentSysMLSubsetting()) || (!node.isPresentMCType()
        && node.isPresentSysMLSubsetting() && node.getSysMLSubsetting().sizeFields() > 1)) {
      int totalParamsToRedefine = 0;
      if(node.isPresentMCType()) {
        RequirementDefSymbol reqDefSym = node.getEnclosingScope().
            resolveRequirementDef(((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName()).get();

        totalParamsToRedefine = reqDefSym.getAstNode().getTotalParamsToRedefine(totalParamsToRedefine);
        if(totalParamsToRedefine > 0 && (!node.isPresentParameterList()
            || node.getParameterList().sizeSysMLParameters() < totalParamsToRedefine)) {
          Log.error("RequirementUsage '" + node.getName() + "' has multiple "
                  + "generalized parameterized requirements, but does not redefine all of the parameters of the general "
                  + "requirements.",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }

      if(node.isPresentSysMLSubsetting()) {
        for (ASTMCQualifiedName subsetted : node.getSysMLSubsetting().getFieldsList()) {
          RequirementUsageSymbol reqUsageSym = node.getEnclosingScope().
              resolveRequirementUsage(subsetted.getQName()).get();

          totalParamsToRedefine = reqUsageSym.getAstNode().getTotalParamsToRedefine(totalParamsToRedefine);

          if(totalParamsToRedefine > 0 && (!node.isPresentParameterList()
              || node.getParameterList().sizeSysMLParameters() < totalParamsToRedefine)) {
            Log.error("RequirementUsage '" + node.getName() + "' has multiple "
                    + "generalized parameterized requirements, but does not redefine all of the parameters of the general "
                    + "requirements.",
                node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          }
        }
      }
    }
  }
}
