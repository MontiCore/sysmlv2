package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

/**
 * Validate requirement parameters for a requirement definition using the following rules:
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
 * will come last in the order). Then, the requirement can optionally declare new parameters.
 */
public class SpecializedReqDefRedefinesInheritedParams
    implements SysMLRequirementDiagramsASTRequirementDefCoCo {

  @Override
  public void check(ASTRequirementDef node) {
    if(node.isPresentSysMLSpecialization()) {
      // Validate that in case of multiple owned generalizations, all parameters are redefined.
      if(node.getSysMLSpecialization().sizeSuperDef() > 1) {
        int totalParamsToRedefine = 0;
        for (ASTMCObjectType superDef : node.getSysMLSpecialization().getSuperDefList()) {
          RequirementDefSymbol reqDefSym = node.getEnclosingScope().
              resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName()).get();
          totalParamsToRedefine = reqDefSym.getAstNode().getTotalParamsToRedefine(totalParamsToRedefine);

          if(totalParamsToRedefine > 0 && (!node.isPresentParameterList()
              || node.getParameterList().sizeSysMLParameters() < totalParamsToRedefine)) {
            Log.error("RequirementDefinition '" + node.getName() + "' specializes multiple "
                    + "parameterized requirements, but does not redefine all of the parameters of the general "
                    + "requirements.",
                node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          }
        }
      }
    }
  }
}
