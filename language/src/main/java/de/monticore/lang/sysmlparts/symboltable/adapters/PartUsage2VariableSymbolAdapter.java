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

  protected PartUsageSymbol getAdaptee() {
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
    PartUsage2VariableSymbolAdapter clone =
        new PartUsage2VariableSymbolAdapter(this.getAdaptee());

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
