package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SpecializedReqUsageRedefinesInheritedParams
    implements SysMLRequirementDiagramsASTRequirementUsageCoCo {

  @Override
  public void check(ASTRequirementUsage node) {
      /*
      If there is only one owned generalization, then it is allowed for the specialized
      requirement to not redefine inherited parameters. If it does redefine parameters,
      then it is allowed for it to redefine fewer parameters than the inherited ones.
      If it wants to declare new parameters, then it must first redefine all inherited parameters.

      If there are multiple owned generalizations, then it is required for the specialized
      requirement to redefine all inherited parameters. It can then declare new parameters of its own.
       */

    // Perform check only if there are multiple owned generalizations.
    if((node.isPresentMCType() && node.isPresentSysMLSubsetting())
        || (node.isPresentSysMLSubsetting() && node.getSysMLSubsetting().sizeFields() > 1)) {
      int maxParamLength = 0;
      if(node.isPresentMCType()) {
        String reqName = ((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName();
        Optional<RequirementDefSymbol> symOpt = node.getEnclosingScope().resolveRequirementDef(
            reqName);
        if(symOpt.isPresent()) {
          RequirementDefSymbol sym = symOpt.get();
          if(sym.getAstNode().isPresentParameterList()) {
            int paramLength = sym.getAstNode().getParameterList().sizeSysMLParameters();
            maxParamLength = Math.max(maxParamLength, paramLength);
          }
        }
        else {
          Log.error("RequirementDefinition '" + reqName + "' could not be resolved.",
              node.getMCType().get_SourcePositionStart(),
              node.getMCType().get_SourcePositionEnd());
        }
      }
      for (ASTMCQualifiedName subsettedUsage : node.getSysMLSubsetting().getFieldsList()) {
        maxParamLength = getMaxParamLength(node, subsettedUsage, maxParamLength);
      }
      if(maxParamLength > 0 && (!node.isPresentParameterList()
          || node.getParameterList().sizeSysMLParameters() < maxParamLength)) {
        Log.error("RequirementUsage '" + node.getName() + "' featuretypes/subsets multiple "
                + "parameterized requirements, but does not redefine all of the inherited parameters.",
            node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
  }

  private int getMaxParamLength(ASTRequirementUsage node, ASTMCQualifiedName mCQualifiedName,
                                int maxParamLength) {
    Optional<RequirementUsageSymbol> symOpt = node.getEnclosingScope().resolveRequirementUsage(
        mCQualifiedName.getQName());
    if(symOpt.isPresent()) {
      RequirementUsageSymbol sym = symOpt.get();
      if(sym.getAstNode().isPresentParameterList()) {
        int paramLength = sym.getAstNode().getParameterList().sizeSysMLParameters();
        maxParamLength = Math.max(maxParamLength, paramLength);
      }
    }
    else {
      Log.error("RequirementUsage '" + mCQualifiedName.getQName() + "' could not be resolved.",
          node.getMCType().get_SourcePositionStart(),
          node.getMCType().get_SourcePositionEnd());
    }
    return maxParamLength;
  }

}
