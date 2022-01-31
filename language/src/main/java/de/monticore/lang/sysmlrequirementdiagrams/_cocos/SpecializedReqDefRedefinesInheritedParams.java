package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class SpecializedReqDefRedefinesInheritedParams
    implements SysMLRequirementDiagramsASTRequirementDefCoCo {

  @Override
  public void check(ASTRequirementDef node) {
    if(node.isPresentSysMLSpecialization()) {
      List<ASTMCObjectType> superDefList = node.getSysMLSpecialization().getSuperDefList();
      /*
      If there is only one owned generalization, then it is allowed for the specialized
      requirement to not redefine inherited parameters. If it does redefine parameters,
      then it is allowed for it to redefine fewer parameters than the inherited ones.
      If it wants to declare new parameters, then it must first redefine all inherited parameters.

      If there are multiple owned generalizations, then it is required for the specialized
      requirement to redefine all inherited parameters. It can then declare new parameters of its own.
       */
      // Perform check only if there are multiple owned generalizations.
      if(superDefList.size() > 1) {
        int maxParamLength = 0;
        for (ASTMCObjectType superDef : superDefList) {
          String reqName = ((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName();
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
                superDef.get_SourcePositionStart(),
                superDef.get_SourcePositionEnd());
          }
        }
        if(maxParamLength > 0 && (!node.isPresentParameterList()
            || node.getParameterList().sizeSysMLParameters() < maxParamLength)) {
          Log.error("RequirementDefinition '" + node.getName() + "' specializes multiple "
                  + "parameterized requirements, but does not redefine all of the inherited parameters.",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
    // Check if there are bindings present. No binding are allowed for parameters in definitions.
    if(node.isPresentParameterList()) {
      for (ASTSysMLParameter param : node.getParameterList().getSysMLParameterList()) {
        if(param.isPresentBinding()) {
          Log.error("RequirementDefinition '" + node.getName() + "' has a parameter "
                  + "'" + param.getName() + "' with a FeatureValue. FeatureValues are not allowed in definitions.",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
  }
}
