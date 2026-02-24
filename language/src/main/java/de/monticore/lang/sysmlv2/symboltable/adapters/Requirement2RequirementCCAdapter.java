/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementUsageSymbol;

public class Requirement2RequirementCCAdapter extends RequirementSymbol  {

  public Requirement2RequirementCCAdapter(RequirementUsageSymbol requirementUsageSymbol) {
    super(requirementUsageSymbol.getFullName());
  }

}
