/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.SourcePosition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SysML does not properly distinguish between types and instances of types
 */
public class AttributeUsage2TypeSymbolAdapter extends OOTypeSymbol {
  protected AttributeUsageSymbol adaptee;

  public AttributeUsage2TypeSymbolAdapter(AttributeUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  protected AttributeUsageSymbol getAdaptee() {
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

  public AttributeUsage2TypeSymbolAdapter deepClone() {
    AttributeUsage2TypeSymbolAdapter clone = new AttributeUsage2TypeSymbolAdapter(
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
