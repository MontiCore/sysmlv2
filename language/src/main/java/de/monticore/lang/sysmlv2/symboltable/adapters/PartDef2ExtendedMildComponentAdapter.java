package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._symboltable.AutomatonSymbol;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;

import javax.swing.plaf.nimbus.State;
import java.util.List;
import java.util.Optional;

public class PartDef2ExtendedMildComponentAdapter extends MildComponentSymbol {
  private final PartDef2ComponentAdapter delegate;

  /** An automaton is cached because we use in subsequent adapters SysML specific operations (i.e. deriver) that are
   * only reliable after Mill.init() and before another language's Mill.init().
   **/
  private final Optional<AutomatonSymbol> automaton;

  public PartDef2ExtendedMildComponentAdapter(PartDefSymbol adaptee) {
    super(adaptee.getName());
    delegate = new PartDef2ComponentAdapter(adaptee);

    automaton = ((ISysMLv2Scope)getAdaptee().getSpannedScope())
            .getLocalStateUsageSymbols()
            .stream()
            .filter(StateUsageSymbol::isExhibited)
            .findFirst()
            .map(state -> new StateUsage2AutomatonAdapter(getAdaptee(), state));
  }

  public PartDef2ComponentAdapter getDelegate() {
    return delegate;
  }

  public PartDefSymbol getAdaptee() {
    return getDelegate().getAdaptee();
  }

  @Override
  public AutomatonSymbol getAutomaton() {
    return automaton.get();
  }

  @Override
  public String getFullName() {
    return getDelegate().getFullName();
  }
  @Override
  public ISysMLv2Scope getEnclosingScope(){
    return getDelegate().getEnclosingScope();
  }

  @Override
  public ISysMLv2Scope getSpannedScope(){
    return getDelegate().getSpannedScope();
  }

  @Override
  public List<VariableSymbol> getParameterList(){
    return getDelegate().getParameterList();
  }

  @Override
  public List<PortSymbol> getPorts(){
    return getDelegate().getPorts();
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters(){
    return getDelegate().getTypeParameters();
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents(){
    return getDelegate().getSubcomponents();
  }

  @Override
  public List<CompKindExpression> getRefinementsList(){
    return getDelegate().getRefinementsList();
  }

  @Override
  public  List<ASTConnector> getConnectorsList(){
    return getDelegate().getConnectorsList();
  }

  @Override
  public boolean equals(Object other) {
    return getDelegate().equals(other);
  }

  @Override
  public int hashCode() {
    return getDelegate().hashCode();
  }
}
