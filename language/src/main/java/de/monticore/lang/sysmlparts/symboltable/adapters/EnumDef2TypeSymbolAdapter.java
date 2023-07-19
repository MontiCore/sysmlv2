/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.lang.sysmlparts._symboltable.EnumDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.SourcePosition;

import java.util.List;
import java.util.stream.Collectors;

public class EnumDef2TypeSymbolAdapter extends OOTypeSymbol {
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
  public IOOSymbolsScope getSpannedScope() {
    return getAdaptee().getSpannedScope();
  }

  /** Nur das richtige spannedScope verwenden */
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
    return getSpannedScope().getLocalFieldSymbols();
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
  public boolean isIsEnum() {
    return true;
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
