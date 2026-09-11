package de.monticore.lang.kermlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;

/**
 * Adapts a KerML datatype to a {@link TypeSymbol} so that relations such as
 * datatype specialization can be represented as super types.
 */
public class Datatype2TypeSymbolAdapter extends TypeSymbol {
  protected DatatypeSymbol adaptee;

  public Datatype2TypeSymbolAdapter(DatatypeSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
    IBasicSymbolsScope spanned = BasicSymbolsMill.scope();
    spanned.setName(adaptee.getName());
    this.setSpannedScope(spanned);
  }

  public DatatypeSymbol getAdaptee() {
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
  public SourcePosition getSourcePosition() {
    return getAdaptee().getSourcePosition();
  }
}
