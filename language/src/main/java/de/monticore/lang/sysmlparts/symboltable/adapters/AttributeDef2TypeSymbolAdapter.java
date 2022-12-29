/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlparts._symboltable.AttributeDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.EnumDefSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;

public class AttributeDef2TypeSymbolAdapter extends TypeSymbol {
  protected AttributeDefSymbol adaptee;

  public AttributeDef2TypeSymbolAdapter(AttributeDefSymbol adaptee){
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  protected AttributeDefSymbol getAdaptee() {
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
  public IBasicSymbolsScope getSpannedScope() {
    return getAdaptee().getSpannedScope();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return getAdaptee().getSourcePosition();
  }

  // TODO needed?
  public AttributeDef2TypeSymbolAdapter deepClone() {
    AttributeDef2TypeSymbolAdapter clone = new AttributeDef2TypeSymbolAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    return clone;
  }
}
