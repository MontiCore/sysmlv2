package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

/**
 * RequirementSubjectSymbol was added to introduce a few helper methods to be used
 * for subject type checking.
 */
public class RequirementSubjectSymbol extends RequirementSubjectSymbolTOP{

  private TypeCheck typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());
  private SymTypeExpression type = null;

  public RequirementSubjectSymbol(String name) {
    super(name);
  }

  /**
   * Method computes the type of requirement subject.
   *
   * @return SymTypeExpression type of the requirement subject symbol
   */
  public SymTypeExpression getSubjectType() {
    if(type == null) {
      SymTypeExpression exp = null;
      if (this.getAstNode().isPresentMCType()) {
        exp = typeCheck.symTypeFromAST(this.getAstNode().getMCType());
      } else if (this.getAstNode().isPresentBinding()) {
        exp = typeCheck.typeOf(this.getAstNode().getBinding());
      } else {
        Log.error("Subject type could not be determined.");
      }
      type = exp;
    }
    return type;
  }

  /**
   * Checks for compatibility with the requirement subject symbol. Compatibility is computed based on
   * any of the following:
   * 1. Same types
   * 2. Ancestor - descendent relationship
   *
   * @param otherSubject An inherited subject
   * @return true if compatible, false otherwise
   */
  public boolean isCompatible(RequirementSubjectSymbol otherSubject) {
    boolean compatible = true;
    SymTypeExpression inheritedSubjectType = otherSubject.getSubjectType();
    SymTypeExpression currentSubjectType = this.getSubjectType();
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
