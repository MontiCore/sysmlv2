package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.symboltable.adapters.Constraint2SpecificationAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Requirement2SpecificationAdapter;

import java.util.Optional;

public class MildComponentSymbol1 extends MildComponentSymbolTOP {

  public MildComponentSymbol1(String name) {
    super(name);
  }

  /**
   * Aktuell gehen wir davon aus, dass alle Ports die gleiche Kardinalität haben (müssen) und berechnen die
   * Gesamt-Kausalität aus den Ports. MontiBelle bzw. die Transformation nach Isabelle kann gemischte Kausalität
   * noch nicht verarbeiten.
   */
  public boolean isStrongCausal() {
    return getAllPorts().stream().anyMatch(p -> p.isStronglyCausal());
  }

  /**
   * Since we might not know the name of the constraint or requirement, and thus cannot spannedScope.resolve(unknown),
   * this method simply crawls the scope for any matching symbols.
   */
  public Optional<MildSpecificationSymbol> getSpecification() {
    if(getSpannedScope().getLocalMildSpecificationSymbols().size() == 1) {
      return getSpannedScope().getLocalMildSpecificationSymbols().stream().findFirst();
    }
    // TODO only "require"
    else if(((ISysMLv2Scope)getSpannedScope()).getLocalRequirementUsageSymbols().size() == 1) {
      var req = ((ISysMLv2Scope)getSpannedScope()).getLocalRequirementUsageSymbols().stream().findFirst().get();
      return Optional.of(new Requirement2SpecificationAdapter(req));
    }
    // TODO only "assert"
    else if(((ISysMLv2Scope)getSpannedScope()).getLocalConstraintUsageSymbols().size() == 1) {
      var constr = ((ISysMLv2Scope)getSpannedScope()).getLocalConstraintUsageSymbols().stream().findFirst().get();
      return Optional.of(new Constraint2SpecificationAdapter(constr));
    }
    else {
      return Optional.empty();
    }
  }
}
