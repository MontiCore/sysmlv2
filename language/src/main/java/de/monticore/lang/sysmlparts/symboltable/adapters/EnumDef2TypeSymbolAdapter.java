/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlparts._symboltable.EnumDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;

public class EnumDef2TypeSymbolAdapter extends TypeSymbol {
  protected EnumDefSymbol adaptee;

  public EnumDef2TypeSymbolAdapter(EnumDefSymbol adaptee){
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  protected EnumDefSymbol getAdaptee() {
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
  public EnumDef2TypeSymbolAdapter deepClone() {
    EnumDef2TypeSymbolAdapter clone = new EnumDef2TypeSymbolAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    return clone;
  }
}
