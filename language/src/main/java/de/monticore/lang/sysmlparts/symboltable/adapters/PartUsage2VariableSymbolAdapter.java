package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;

public class PartUsage2VariableSymbolAdapter extends VariableSymbol {
  protected PartUsageSymbol adaptee;

  public PartUsage2VariableSymbolAdapter(PartUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  public PartUsageSymbol getAdaptee() {
    return adaptee;
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
  public PartUsage2VariableSymbolAdapter deepClone() {
    var clone = new PartUsage2VariableSymbolAdapter(getAdaptee());
    clone.setAccessModifier(getAccessModifier());
    clone.setEnclosingScope(getEnclosingScope());
    clone.setFullName(getFullName());
    clone.setIsReadOnly(isIsReadOnly());

    if (isPresentAstNode()) {
      clone.setAstNode(getAstNode());
    }

    if (getType() != null) {
      clone.setType(getType().deepClone());
    }

    return clone;
  }
}
