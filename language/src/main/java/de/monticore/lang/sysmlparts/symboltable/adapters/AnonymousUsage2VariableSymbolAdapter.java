/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlbasis._symboltable.AnonymousUsageSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.SourcePosition;

public class AnonymousUsage2VariableSymbolAdapter extends FieldSymbol {
  protected AnonymousUsageSymbol adaptee;

  public AnonymousUsage2VariableSymbolAdapter(AnonymousUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.accessModifier = adaptee.getAccessModifier();
  }

  protected AnonymousUsageSymbol getAdaptee() {
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
  public IOOSymbolsScope getEnclosingScope() {
    return this.getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return this.getAdaptee().getSourcePosition();
  }

  @Override
  public SymTypeExpression getType() {
    return getAdaptee().getTypes(0);
  }

  @Override
  public AnonymousUsage2VariableSymbolAdapter deepClone() {
    AnonymousUsage2VariableSymbolAdapter clone = new AnonymousUsage2VariableSymbolAdapter(this.getAdaptee());
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
