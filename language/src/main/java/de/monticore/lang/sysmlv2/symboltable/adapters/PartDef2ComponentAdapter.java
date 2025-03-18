package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector._ast.ASTConnector;
import de.monticore.lang.componentconnector._symboltable.AutomatonSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildSpecificationSymbol;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.KindOfComponent;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartDef2ComponentAdapter extends MildComponentSymbol {

  protected PartDefSymbol adaptee;

  /**
   * An automaton is cached because we use in subsequent adapters SysML
   * specific operations (i.e. deriver) that are only reliable after
   * Mill.init() and before another language's Mill.init().
   */
  private final Optional<AutomatonSymbol> automaton;

  public PartDef2ComponentAdapter(PartDefSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;

    automaton = ((ISysMLv2Scope)getAdaptee().getSpannedScope())
        .getLocalStateUsageSymbols()
        .stream()
        .filter(StateUsageSymbol::isExhibited)
        .findFirst()
        .map(state -> new StateUsage2AutomatonAdapter(getAdaptee(), state));
  }

  public PartDefSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) getAdaptee().getEnclosingScope();
  }

  @Override
  public ISysMLv2Scope getSpannedScope() {
    return (ISysMLv2Scope) getAdaptee().getSpannedScope();
  }

  @Override
  public List<VariableSymbol> getParameterList() {
    return getAdaptee().getSpannedScope().getLocalAttributeUsageSymbols().stream()
        .filter(a -> a.isPresentAstNode())
        .filter(a -> a.getAstNode().getModifier().isFinal())
        .map(a -> new AttributeUsage2VariableSymbolAdapter(a))
        .collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getPorts() {
    var ins = getAdaptee().getSpannedScope().getLocalPortUsageSymbols().stream()
        .flatMap(pu ->
            pu.getInputAttributes().stream()
                .map(au -> new AttributeUsage2PortSymbolAdapter(au, pu,true))
        );
    var outs = getAdaptee().getSpannedScope().getLocalPortUsageSymbols().stream()
        .flatMap(pu ->
            pu.getOutputAttributes().stream()
                .map(au -> new AttributeUsage2PortSymbolAdapter(au, pu,false))
        );

    return Stream.concat(ins, outs).collect(Collectors.toList());
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return getSpannedScope().getLocalTypeVarSymbols();
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return getSpannedScope().getLocalPartUsageSymbols().stream()
        .map(PartUsage2SubcomponentAdapter::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<CompKindExpression> getRefinementsList() {
    List<CompKindExpression> res = new ArrayList<>();
    for(var partDefSymbol: adaptee.getDirectRefinements()) {
      var compSymbol = ((ISysMLv2Scope)partDefSymbol.getEnclosingScope()).resolveComponent(partDefSymbol.getName());
      if(compSymbol.isPresent()) {
        res.add(new KindOfComponent(compSymbol.get()));
      }
    }
    return res;
  }

  @Override
  public  List<ASTConnector> getConnectorsList() {
    if(adaptee.isPresentAstNode()) {
      var sysmlConnectors = adaptee.getAstNode().getSysMLElements(ASTConnectionUsage.class);
      return sysmlConnectors.stream()
          .map(ConnectorWrapper::build)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
    }
    else {
      Log.warn("0x10004 Attempted to get connectors for a symbol");
      return new ArrayList<>();
    }
  }


  /**
   * Since we might not know the name of the constraint or requirement, and thus
   * cannot spannedScope.resolve(unknown), this method simply crawls the scope
   * for any matching symbols.
   */
  @Override
  public MildSpecificationSymbol getSpecification() {
    if(getSpannedScope().getLocalMildSpecificationSymbols().size() == 1) {
      return getSpannedScope().getLocalMildSpecificationSymbols().stream()
          .findFirst().get();
    }
    // TODO only "require"
    else if((getSpannedScope()).getLocalRequirementUsageSymbols().size() == 1) {
      var req = getSpannedScope().getLocalRequirementUsageSymbols().stream()
          .findFirst().get();
      return new Requirement2SpecificationAdapter(req);
    }
    // TODO only "assert"
    else if((getSpannedScope()).getLocalConstraintUsageSymbols().size() == 1) {
      var constr = getSpannedScope().getLocalConstraintUsageSymbols().stream()
          .findFirst().get();
      return new Constraint2SpecificationAdapter(constr);
    }
    else {
      Log.error("0xB0001 Specification was empty");
      return null;
    }
  }

  @Override
  public boolean isStateBased() {
    return automaton.isPresent();
  }

  @Override
  public AutomatonSymbol getAutomaton() {
    return automaton.get();
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof ComponentSymbol) {
      return ((ComponentSymbol)other).getFullName().equals(this.getFullName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.getFullName().hashCode();
  }

}
