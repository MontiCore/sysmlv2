package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlactions._symboltable.CalcDefSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.se_rwth.commons.SourcePosition;

/**
 * Adapts a SysML {@link CalcDefSymbol} to a MontiCore {@link FunctionSymbol}.
 *
 * MontiCore's expression type checker resolves function calls against
 * {@link FunctionSymbol}s from BasicSymbols. SysML calc def, however, are represented by
 * {@link CalcDefSymbol}s. This adapter exposes a calc def as a function symbol so that
 * expressions such as {@code f.bar()} can be resolved and type-checked using the existing
 * function-call infrastructure.
 */
public class CalcDef2FunctionSymbolAdapter extends FunctionSymbol {
  protected CalcDefSymbol adaptee;

  public CalcDef2FunctionSymbolAdapter(CalcDefSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;

    setAccessModifier(adaptee.getAccessModifier());

    IBasicSymbolsScope spannedScope = BasicSymbolsMill.scope();
    spannedScope.setName(adaptee.getName());
    spannedScope.setShadowing(true);
    spannedScope.setEnclosingScope(adaptee.getEnclosingScope());
    setSpannedScope(spannedScope);

    setType(adaptee.getReturnType());
  }

  protected CalcDefSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public String getName() {
    return adaptee.getName();
  }

  @Override
  public String getFullName() {
    return adaptee.getFullName();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return adaptee.getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return adaptee.getSourcePosition();
  }
}
