/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlv2.symboltable.adapters.AttributeUsage2PortSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;

public class PortUsage2VariableSymbolAdapter extends VariableSymbol {
  protected PortUsageSymbol adaptee;

  public PortUsage2VariableSymbolAdapter(PortUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  protected PortUsageSymbol getAdaptee() {
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
  public PortUsage2VariableSymbolAdapter deepClone() {
    PortUsage2VariableSymbolAdapter clone = new PortUsage2VariableSymbolAdapter(this.getAdaptee());
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

  @Override
  public boolean equals(Object other) {
    if(other instanceof PortUsage2VariableSymbolAdapter) {
      return this.adaptee.equals(((PortUsage2VariableSymbolAdapter) other).adaptee);
    }
    else {
      return super.equals(other);
    }
  }

  @Override
  public int hashCode() {
    return adaptee.hashCode();
  }
}
