/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.lang.sysmlparts._symboltable.AttributeDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.SourcePosition;

import java.util.List;
import java.util.stream.Collectors;

public class AttributeDef2TypeSymbolAdapter extends OOTypeSymbol {
  protected AttributeDefSymbol adaptee;

  public AttributeDef2TypeSymbolAdapter(AttributeDefSymbol adaptee) {
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
  public IOOSymbolsScope getSpannedScope() {
    return getAdaptee().getSpannedScope();
  }

  @Override
  public IOOSymbolsScope getEnclosingScope() {
    return getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return getAdaptee().getSourcePosition();
  }

  @Override
  public List<FieldSymbol> getFieldList() {
    // ersetzt das "spannedScope == null" - wir können nicht auf das private Feld zugreifen und machen deswegen diesen
    // Murks.
    try {
      getSpannedScope();
    }
    catch(NullPointerException ex) {
      return Lists.newArrayList();
    }

    var attributes = ((ISysMLv2Scope)getSpannedScope()).getLocalAttributeUsageSymbols();
    return attributes.stream().map(a -> new AttributeUsage2VariableSymbolAdapter(a)).collect(Collectors.toList());
  }

  // TODO needed?
  public AttributeDef2TypeSymbolAdapter deepClone() {
    AttributeDef2TypeSymbolAdapter clone = new AttributeDef2TypeSymbolAdapter(
        this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    return clone;
  }
}
