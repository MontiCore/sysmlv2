package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.sysml4verification._symboltable.ConstraintUsageSymbol;
import de.monticore.lang.sysml4verification._symboltable.RequirementUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.symboltable.adapters.Constraint2SpecificationAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Requirement2SpecificationAdapter;

import java.util.List;
import java.util.Optional;

public class MildComponentSymbol extends MildComponentSymbolTOP {

  public MildComponentSymbol(String name) {
    super(name);
  }

  /**
   * Aktuell gehen wir davon aus, dass alle Ports die gleiche Kardinalität haben
   * (müssen) und berechnen die Gesamt-Kausalität aus den Ports. MontiBelle bzw.
   * die Transformation nach Isabelle kann gemischte Kausalität noch nicht
   * verarbeiten.
   */
  public boolean isStrongCausal() {
    return getAllPorts().stream().anyMatch(p -> p.isStronglyCausal());
  }

  public boolean isHistoryBased() {
    return !getSpannedScope().getLocalMildSpecificationSymbols().isEmpty();
  }

  /**
   * Since we might not know the name of the constraint or requirement, and thus
   * cannot spannedScope.resolve(unknown), this method simply crawls the scope
   * for any matching symbols.
   */
  public Optional<MildSpecificationSymbol> getSpecification() {
    var localSpecs = getSpannedScope().getLocalMildSpecificationSymbols();
    var reqUsages = ((ISysMLv2Scope) getSpannedScope())
        .getLocalRequirementUsageSymbols();
    var constraintUsages = ((ISysMLv2Scope) getSpannedScope())
        .getLocalConstraintUsageSymbols();
    if(localSpecs.size() == 1) {
      return localSpecs.stream().findFirst();
    }
    else if(reqUsages.size() == 1) {
      var req = reqUsages.stream().findFirst().get();
      return Optional.of(new Requirement2SpecificationAdapter(req));
    }
    else if(constraintUsages.size() == 1) {
      var constr = constraintUsages.stream().findFirst().get();
      return Optional.of(new Constraint2SpecificationAdapter(constr));
    }
    else {
      return Optional.empty();
    }
  }

  public boolean isTsynStateBased() {
    return !getSpannedScope().getLocalAutomatonSymbols().isEmpty();
  }

  public AutomatonSymbol getAutomaton() {
    return getSpannedScope().getLocalAutomatonSymbols().stream()
        .findFirst().get();
  }

  public boolean isEventBased() {
    return !getSpannedScope().getLocalEventAutomatonSymbols().isEmpty();
  }

  public EventAutomatonSymbol getEventAutomaton() {
    return getSpannedScope().getLocalEventAutomatonSymbols().stream()
        .findFirst().get();
  }
}
