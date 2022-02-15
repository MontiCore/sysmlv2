package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ASTBaseRequirement is an abstract class that encapsulates the common logic for requirements.
 * As of now, this logic is limited to finalizing correct requirement parameters.
 */
public abstract class ASTBaseRequirement extends ASTBaseRequirementTOP {

  /**
   * List of inherited parameters. In cases where partial redefinition of requirement
   * parameters is allowed, the rest of the parameters are then inherited and stored in
   * this list, as well as added to the spanned scope of the requirement.
   */
  protected ArrayList<ASTSysMLParameter> inheritedParameters = null;

  protected TypeCheck typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());

  /**
   * Checks for type compatibility between the given two types.
   *
   * @param type    Possible super type to be checked.
   * @param subType Possible subtype to be checked.
   * @return whether the types are compatible.
   */
  private boolean isCompatible(SymTypeExpression type, SymTypeExpression subType) {
    boolean compatible = true;
    // if the type to compare against is 'Anything', represented by 'null',
    // then everything is compatible to it.
    if(type == null)
      return true;
    // Otherwise, we check for compatibility.
    if(!type.deepEquals(subType)) {
      compatible = false;
      while (!subType.getTypeInfo().isEmptySuperTypes() && !compatible) {
        SymTypeExpression superType = subType.getTypeInfo().getSuperTypesList().get(0);
        if(superType.deepEquals(type)) {
          compatible = true;
        }
        else {
          subType = superType;
        }
      }
    }
    return compatible;
  }

  /**
   * Compute total (potential) parameters to be redefined as a sum of size of parameter list
   * and inherited parameters list.
   *
   * @param totalParamsToRedefine int
   * @return int
   */
  public int getTotalParamsToRedefine(int totalParamsToRedefine
  ) {
    int currentTotal = 0;

    if(this.isPresentParameterList()) {
      currentTotal += this.getParameterList().sizeSysMLParameters();
    }

    if(this.isPresentInheritedParameters()) {
      currentTotal += this.sizeInheritedParameters();
    }
    totalParamsToRedefine = Math.max(totalParamsToRedefine, currentTotal);

    return totalParamsToRedefine;
  }

  /**
   * Validates the type compatibility between redefining and redefined parameters.
   *
   * @param parameters ASTParameterList
   */
  public void validateRedefiningDefinitionParameters(List<ASTSysMLParameter> parameters) {
    for (int i = 0; i < this.getParameterList().sizeSysMLParameters() && i < parameters.size(); ++i) {
      // If the types aren't compatible, raise an error.
      if(!isCompatible(
          parameters.get(i).getSymbol().getType(),
          this.getParameterList().getSysMLParameter(i).getSymbol().getType())) {
        Log.error("RequirementParameter '" + parameters.get(i).getName() + "' has type '"
                + parameters.get(i).getSymbol().getType().getTypeInfo().getName() + "', but was redefined with type '"
                + this.getParameterList().getSysMLParameter(i).getSymbol().getType().getTypeInfo().getName() + "', "
                + "which is not compatible.",
            this.getParameterList().getSysMLParameter(i).get_SourcePositionStart(),
            this.getParameterList().getSysMLParameter(i).get_SourcePositionEnd());
      }
    }
  }

  /**
   * Validates the type compatibility between redefining and redefined parameters.
   * Also validates type compatibility with any feature values, if present.
   *
   * @param parameters ASTParameterList
   */
  public void validateRedefiningUsageParameters(List<ASTSysMLParameter> parameters) {
    for (int i = 0; i < this.getParameterList().sizeSysMLParameters() && i < parameters.size(); ++i) {
      // If the types aren't compatible, raise an error.
      if(!isCompatible(
          parameters.get(i).getSymbol().getType(),
          this.getParameterList().getSysMLParameter(i).getSymbol().getType())) {
        Log.error("RequirementParameter '" + parameters.get(i).getName() + "' has type '"
                + parameters.get(i).getSymbol().getType().getTypeInfo().getName() + "', but was redefined with type '"
                + this.getParameterList().getSysMLParameter(i).getSymbol().getType().getTypeInfo().getName() + "', "
                + "which is not compatible.",
            this.getParameterList().getSysMLParameter(i).get_SourcePositionStart(),
            this.getParameterList().getSysMLParameter(i).get_SourcePositionEnd());
      }
      // If the assigned feature value isn't compatible, raise an error.
      if(this.getParameterList().getSysMLParameter(i).isPresentBinding()) {
        SymTypeExpression expType = typeCheck.typeOf(this.getParameterList().getSysMLParameter(i).getBinding());
        if(!isCompatible(
            this.getParameterList().getSysMLParameter(i).getSymbol().getType(), expType)) {
          Log.error("RequirementParameter '" + parameters.get(i).getName() + "' has type '"
                  + this.getParameterList().getSysMLParameter(i).getSymbol().getType().getTypeInfo().getName()
                  + "', but was assigned a value of"
                  + " type '" + expType.getTypeInfo().getName() + "', which is not compatible.",
              this.getParameterList().getSysMLParameter(i).get_SourcePositionStart(),
              this.getParameterList().getSysMLParameter(i).get_SourcePositionEnd());
        }
      }
    }
  }

  /**
   * In case where partial redefinition of parameters is allowed, the rest of the
   * parameters are then inherited.
   * 1. We make a clone of the parameters to be inherited,
   * 2. store them in inheritedParameters list,
   * 3. and add them in the requirement's spanned scope.
   *
   * @param generalization ASTBaseRequirement
   */
  public void inheritNonRedefinedParameters(ASTBaseRequirement generalization) {
    inheritedParameters = new ArrayList<>();

    // Total parameters defined by the generalized requirement.
    int parameterCount = 0;
    if(generalization.isPresentParameterList()) {
      parameterCount += generalization.getParameterList().sizeSysMLParameters();
    }
    if(generalization.isPresentInheritedParameters()) {
      parameterCount += generalization.sizeInheritedParameters();
    }

    int redefinedParameterCount = 0;
    if(this.isPresentParameterList()) {
      // Find out how many parameters were redefined and how many need to be inherited.
      redefinedParameterCount = this.getParameterList().sizeSysMLParameters();
    }

    // Parameters will only be inherited if they weren't redefined.
    if(redefinedParameterCount < parameterCount) {
      ArrayList<ASTSysMLParameter> parameters = new ArrayList<>();
      if(generalization.isPresentParameterList()) {
        parameters.addAll(generalization.getParameterList().getSysMLParameterList());
      }
      if(generalization.isPresentInheritedParameters()) {
        parameters.addAll(generalization.getInheritedParameters());
      }

      if(parameters.size() > 0) {
        //        inheritedParameters = new ArrayList<>();
        // If the current requirement does not redefine any parameters, then we add all of them.
        int startIndex = 0;
        // Otherwise, we compute the starting position of params. to add and start from there.
        if(this.isPresentParameterList()) {
          startIndex = this.getParameterList().sizeSysMLParameters();
        }
        for (int i = startIndex; i < parameters.size(); ++i) {
          ASTSysMLParameter clone = parameters.get(i).deepClone();
          clone.setEnclosingScope(this.getSpannedScope());
          inheritedParameters.add(clone);
          this.getSpannedScope().add(clone.getSymbol());
        }
      }
    }
  }

  /**
   * Check if inherited parameters are present.
   *
   * @return boolean
   */
  public boolean isPresentInheritedParameters() {
    return this.sizeInheritedParameters() > 0;
  }

  /**
   * Return size of inherited parameters.
   *
   * @return int
   */
  public int sizeInheritedParameters() {
    return this.getInheritedParameters().size();
  }

  public abstract ArrayList<ASTSysMLParameter> getInheritedParameters();

  /**
   * Return inherited parameter present at the provided index.
   *
   * @param i int
   * @return ASTSysMLParameter
   */
  public ASTSysMLParameter getInheritedParameter(int i) {
    return this.inheritedParameters.get(i);
  }
}
