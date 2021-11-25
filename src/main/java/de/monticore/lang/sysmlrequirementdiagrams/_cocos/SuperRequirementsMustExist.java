package de.monticore.lang.sysmlrequirementdiagrams._cocos;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * CoCo verifies that, if a requirement definition specializes other requirement definitions,
 * then these general definitions must be resolvable.
 */
public class SuperRequirementsMustExist implements SysMLRequirementDiagramsASTRequirementDefCoCo {

  @Override
  public void check(ASTRequirementDef node) {
    if (node.isPresentSysMLSpecialization()) {
      ASTSysMLSpecialization specialization = node.getSysMLSpecialization();
      for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
        String superReqName = ((ASTMCQualifiedType) superDef).getMCQualifiedName().toString();
        Optional<RequirementDefSymbol> superDefSym = ((SysMLv2Scope) specialization.getEnclosingScope())
            .resolveRequirementDef(superReqName);

        if (!superDefSym.isPresent()) {
          Log.error("Requirement definition '" + node.getName() + "' specializes requirement definition '" +
                  superReqName + "', but the latter could not be resolved!",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
  }
}
