package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;

public class ASTRequirementDef extends ASTRequirementDefTOP {

  /**
   * Inherit those parameters from the generalized requirements that weren't redefined
   * by the current requirement definition.
   */
  public void inheritNonRedefinedRequirementDefinitionParameters() {
    if(this.isPresentSysMLSpecialization() && this.getSysMLSpecialization().sizeSuperDef() == 1) {
    /*
    Since the current requirement def. only generalizes single requirement def.,
    it is allowed that less than the total number of super requirement parameters
    are redefined here.
    The parameters that aren't redefined will be inherited instead.
     */
      ASTSysMLSpecialization specialization = this.getSysMLSpecialization();
      RequirementDefSymbol sym = this.getEnclosingScope().
          resolveRequirementDef(
              ((ASTMCQualifiedType) specialization.getSuperDef(0)).getMCQualifiedName().getQName()).get();

      this.inheritNonRedefinedParameters(sym.getAstNode());
    }
    else {
      this.inheritedParameters = new ArrayList<>();
    }
  }

  /**
   * Return inherited parameters.
   * This method recursively adds inherited parameters in the current node,
   * as well as any owned generalizations, if its inheritedParameters field is uninitialized.
   *
   * @return ArrayList<ASTSysMLParameter>
   */
  @Override
  public ArrayList<ASTSysMLParameter> getInheritedParameters() {
    if(this.inheritedParameters == null) {
      inheritNonRedefinedRequirementDefinitionParameters();
    }
    return this.inheritedParameters;
  }

}
