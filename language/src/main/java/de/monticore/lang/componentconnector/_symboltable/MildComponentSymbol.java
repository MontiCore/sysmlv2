package de.monticore.lang.componentconnector._symboltable;

import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.symboltable.adapters.Constraint2SpecificationAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Requirement2SpecificationAdapter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.stream.Collectors;

public class MildComponentSymbol extends MildComponentSymbolTOP {

  public MildComponentSymbol(String name) {
    super(name);
  }

  /**
   * Helfer-Funktion, die transitiv den Beginn einer Verfeinerungskette bestimmt.<br>
   *
   * Beispiel: A refines B, C; B refines D; C refines D;
   *           Der eindeutige Start ist D.<br>
   *
   * Eine Komponente ohne explizite Verfeinerungen ist selbst der Start der Kette. Sollte es keinen eindeutigen Start
   * geben (zB. A refines B, C), wird ein Fehler ausgegeben.
   */
  public Optional<MildComponentSymbol> getRefinementStart() {
    if(getRefinementsList() == null || getRefinementsList().isEmpty()) {
      return Optional.of(this);
    }
    else {
      var candidates = getRefinementsList().stream()
          .map(refinement -> refinement.getTypeInfo())
          .filter(component -> component instanceof MildComponentSymbol)
          .map(component -> (MildComponentSymbol)component)
          .map(mildComponent -> mildComponent.getRefinementStart()) // Rekursion
          .filter(optComponent -> optComponent.isPresent())
          .map(optComponent -> optComponent.get())
          .collect(Collectors.toSet());
      if(candidates.size() == 1) {
        return candidates.stream().findFirst();
      }
      else {
        Log.warn("Could not determine a single root component in the refinement chain.");
        return Optional.empty();
      }
    }
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
