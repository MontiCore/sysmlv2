package de.monticore.lang.sysmlrequirementdiagrams;

import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

/**
 * RequirementDiagramsHelper offers static helper methods for processing requirements.
 * TODO Move to RequirementSubjectSymbol via TOP as a non-static function
 */
public class RequirementDiagramsHelper {

  private static TypeCheck typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());

  /**
   * Method computes the type of requirement subject.
   *
   * @param subject RequirementSubjectSymbol whose type is required
   * @return SymTypeExpression type of the requirement subject symbol
   */
  public static SymTypeExpression getSubjectType(RequirementSubjectSymbol subject) {
    SymTypeExpression exp = null;
    if (subject.getAstNode().isPresentMCType()) {
      exp = typeCheck.symTypeFromAST(subject.getAstNode().getMCType());
    } else if (subject.getAstNode().isPresentBinding()) {
      exp = typeCheck.typeOf(subject.getAstNode().getBinding());
    } else {
      Log.error("Subject type could not be determined.");
    }
    return exp;
  }

  /**
   * Checks for compatibility between two requirement subject symbols. Compatibility is computed based on
   * any of the following:
   * 1. Same types
   * 2. Ancestor - descendent relationship
   *
   * @param inherited An inherited subject
   * @param current   Subject that should be compatible with the inherited one
   * @return true if compatible, false otherwise
   */
  public static boolean isCompatible(RequirementSubjectSymbol inherited, RequirementSubjectSymbol current) {
    boolean compatible = true;
    SymTypeExpression inheritedSubjectType = getSubjectType(inherited);
    SymTypeExpression currentSubjectType = getSubjectType(current);
    if (!inheritedSubjectType.deepEquals(currentSubjectType)) {
      compatible = false;
      while (!currentSubjectType.getTypeInfo().isEmptySuperTypes() && !compatible) {
        // TODO: check for multiple super types
        SymTypeExpression superType = currentSubjectType.getTypeInfo().getSuperTypesList().get(0);
        if (superType.deepEquals(inheritedSubjectType)) {
          compatible = true;
        } else {
          currentSubjectType = superType;
        }
      }
    }
    return compatible;
  }
}
