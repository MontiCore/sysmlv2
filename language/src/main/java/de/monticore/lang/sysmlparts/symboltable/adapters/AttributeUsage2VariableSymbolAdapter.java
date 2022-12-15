package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;

public class AttributeUsage2VariableSymbolAdapter extends VariableSymbol {
  protected AttributeUsageSymbol adaptee;

  public AttributeUsage2VariableSymbolAdapter(AttributeUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.accessModifier = adaptee.getAccessModifier();
  }

  protected AttributeUsageSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public void setName(String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.getAdaptee().setName(name);
  }

  @Override
  public String getName() {
    return this.getAdaptee().getName();
  }

  @Override
  public String getFullName() {
    return this.getAdaptee().getFullName();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return this.getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return this.getAdaptee().getSourcePosition();
  }

  @Override
  public AttributeUsage2VariableSymbolAdapter deepClone() {
    AttributeUsage2VariableSymbolAdapter clone = new AttributeUsage2VariableSymbolAdapter(this.getAdaptee());
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
