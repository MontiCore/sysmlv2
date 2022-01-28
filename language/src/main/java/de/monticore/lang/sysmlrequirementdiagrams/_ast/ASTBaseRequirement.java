package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlparametrics._ast.ASTParameterList;
import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * ASTBaseRequirement is an abstract class that encapsulates the common logic for requirements.
 * As of now, this logic is limited to finalizing correct requirement parameters.
 */
public abstract class ASTBaseRequirement extends ASTBaseRequirementTOP {

  /**
   * A map with parameter name as 'key' and 'ASTSysMLParameter' as value.
   * This is a cumulative parameters map (i.e. includes also all the inherited parameters).
   */
  protected HashMap<String, ASTSysMLParameter> parameters =
      new HashMap<>();

  protected TypeCheck typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());

  public HashMap<String, ASTSysMLParameter> getParameters() {
    return parameters;
  }

  /**
   * Transfers the parameters from the ASTParameterList to internally maintained parameters map.
   */
  public void transferParameters() {
    if(this.isPresentParameterList()) {
      HashMap<String, ASTSysMLParameter> parameters = new HashMap<>();
      ASTParameterList params = this.getParameterList();

      /*
      First, add all the parameters in a temporary map.
      This addition ensures that,
       1. there are no duplicates in the parameter list of the current requirement, and
       2. if a value is assigned to a param., then it is compatible with its type.
       */
      for (ASTSysMLParameter param : params.getSysMLParameterList()) {
        if(parameters.containsKey(param.getName())) {
          Log.error("Requirement parameter '" + param.getName()
              + "' cannot be added as it is already present in the parameter list."
              + " It is possible that duplicate parameters were added in the requirement"
              + " parameter list.");
        }

        SymTypeExpression type = null;
        ASTExpression exp = null;
        boolean typeInferred = false;

        // Extract explicitly defined parameter type.
        if(param.isPresentMCType()) {
          type = typeCheck.symTypeFromAST(param.getMCType());
        }

        // If a value was assigned to the parameter, then validate its type is correct.
        if(param.isPresentBinding()) {
          // Extract the type of the assigned value.
          exp = param.getBinding();
          SymTypeExpression expType = typeCheck.typeOf(param.getBinding());
          if(type != null) {
            // Validate that the explicitly defined type and inferred type are compatible.
            if(!isCompatible(type, expType)) {
              Log.error("Requirement parameter '" + param.getName() + "' has a type '" + type.getTypeInfo().getName()
                  + "', but it is assigned a value of type '" + expType.getTypeInfo().getName() + "', which"
                  + " is not compatible!");
            }
          }
          // If there was no explicitly defined type, then assign exp. type to it.
          // Because we have inferred the type from the assigned value, we also set the flag 'typeInferred' to true.
          else {
            type = expType;
            typeInferred = true;
          }
        }

        // Update the original ASTSysMLParameter accordingly & add in the parameters map.
        param.setType(type);
        param.setExpression(exp);
        param.setTypeInferred(typeInferred);
        parameters.put(param.getName(), param);
      }

      /*
      Now, we traverse the temporary map and add these parameters in the (possibly) pre-populated
      parameter map of the requirement.
      Map will be pre-populated at this point if the current requirement specializes/subsets other requirements.
       */
      for (Map.Entry<String, ASTSysMLParameter> entry : parameters.entrySet()) {
        if(this.parameters.containsKey(entry.getKey())) {
          /*
           This parameter was already added as it was inherited in some way.
           Now we check for type compatibility between the old and new types
           and update the parameter accordingly.
           */
          ASTSysMLParameter oldParameter = this.parameters.get(entry.getKey());
          ASTSysMLParameter newParameter = entry.getValue();

          // In case the stored param. already has a type and the new param. also defines a type,
          // then these must be compatible. Constraining the type is allowed.
          if(oldParameter.getType() != null && newParameter.getType() != null
              && !isCompatible(oldParameter.getType(), newParameter.getType())) {
            Log.error("Requirement parameter '" + entry.getKey() + "' has a type '"
                + oldParameter.getType().getTypeInfo().getName()
                + "', but it is defined with a new type or assigned a value of type '"
                + newParameter.getType().getTypeInfo().getName() + "', which is not compatible!");
          }

          // Set type as old type if new type is not present or was inferred.
          if(oldParameter.getType() != null && (newParameter.getType() == null || newParameter.isTypeInferred())) {
            entry.getValue().setType(oldParameter.getType());
            entry.getValue().setTypeInferred(false);
          }
        }
        // We always update the map to point to the new parameter, since it was defined in the requirement's scope.
        this.parameters.put(entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * Checks for type compatibility between the given two types.
   *
   * @param type    Possible super type to be checked.
   * @param subType Possible subtype to be checked.
   * @return whether the types are compatible.
   */
  private boolean isCompatible(SymTypeExpression type, SymTypeExpression subType) {
    boolean compatible = true;
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
   * Adds inherited parameters to the parameter map.
   * By inherited parameters here is meant any parameters passed onto the requirement
   * due to type binding or specialization/subsetting of other requirements.
   *
   * @param parameters Map<String, ASTBaseRequirement.Parameter>
   */
  public void addInheritedParameters(Map<String, ASTSysMLParameter> parameters) {
    for (Map.Entry<String, ASTSysMLParameter> entry : parameters.entrySet()) {
      if(this.parameters.containsKey(entry.getKey())) {
        // If a parameter was inherited from multiple sources, then this parameter must be exactly the same,
        // i.e. its type & expression value. We don't do a comparison on the AST level.
        if(!this.parameters.get(entry.getKey()).isEqual(entry.getValue())) {
          Log.error("Requirement parameter '" + entry.getKey() + "' was inherited multiple times, "
              + "but the the parameters are not equal. Only the same parameters can be inherited more than once.");
        }

        // Check and/or set the correct parameter type.
        SymTypeExpression type = this.parameters.get(entry.getKey()).getType();
        SymTypeExpression newType = entry.getValue().getType();

        // We set the parameter type to the previously known type if there is a new type present, but
        // the type was inferred, not defined. Consequently, the map is also updated with this modified parameter.
        if(entry.getValue().isTypeInferred()) {
          entry.getValue().setType(type);
          entry.getValue().setTypeInferred(false);
          this.parameters.put(entry.getKey(), entry.getValue());
        }
      }
      // Simply add the parameter if it was not present.
      else {
        this.parameters.put(entry.getKey(), entry.getValue());
      }
    }
  }

}
