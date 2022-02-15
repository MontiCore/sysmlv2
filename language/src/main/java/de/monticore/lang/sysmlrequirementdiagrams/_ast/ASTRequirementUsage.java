package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;

public class ASTRequirementUsage extends ASTRequirementUsageTOP {
  /**
   * Inherit those parameters from the generalized requirements that weren't redefined
   * by the current requirement usage.
   */
  public void inheritNonRedefinedRequirementUsageParameters() {
    /*
    If the requirement usage only has a single type binding or subsets a single requirement usage,
    then it is allowed that less than total defined parameters of the generalization are redefined
    here.
    The parameters that aren't redefined will be inherited instead.
     */
    if(this.isPresentMCType() && !this.isPresentSysMLSubsetting()) {
      RequirementDefSymbol sym = this.getEnclosingScope().
          resolveRequirementDef(((ASTMCQualifiedType) this.getMCType()).getMCQualifiedName().getQName()).get();
      this.inheritNonRedefinedParameters(sym.getAstNode());
    }
    else if(!this.isPresentMCType() && this.isPresentSysMLSubsetting() && this.getSysMLSubsetting().sizeFields() == 1) {
      RequirementUsageSymbol sym = this.getEnclosingScope().
          resolveRequirementUsage(this.getSysMLSubsetting().getFields(0).getQName()).get();
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
      inheritNonRedefinedRequirementUsageParameters();
    }
    return this.inheritedParameters;
  }

}
