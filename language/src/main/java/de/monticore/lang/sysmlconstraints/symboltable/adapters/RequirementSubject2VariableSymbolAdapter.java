/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlconstraints.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementSubjectSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

public class RequirementSubject2VariableSymbolAdapter extends VariableSymbol {
  protected RequirementSubjectSymbol adaptee;

  public RequirementSubject2VariableSymbolAdapter(RequirementSubjectSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  protected RequirementSubjectSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public SymTypeExpression getType() {
    var types = this.adaptee.getTypesList();
    if(types.size() != 1) {
      Log.trace("Experiencing Subj. with > 1 types", getClass().getName());
      return null;
    }
    return types.get(0);
  }

  @Override
  public void setName(String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    getAdaptee().setName(name);
  }

  @Override
  public String getName() {
    return getAdaptee().getName();
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return getAdaptee().getSourcePosition();
  }

  @Override
  public RequirementSubject2VariableSymbolAdapter deepClone() {
    RequirementSubject2VariableSymbolAdapter clone = new RequirementSubject2VariableSymbolAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    clone.setIsReadOnly(this.isIsReadOnly());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if (this.getType() != null) {
      clone.setType(this.getType().deepClone());
    }
    return clone;
  }
}
