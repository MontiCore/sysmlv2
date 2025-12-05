package de.monticore.lang.componentconnector._symboltable;

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

  public MildSpecificationSymbol getSpecification() {
    return getSpannedScope().getLocalMildSpecificationSymbols().stream()
        .findFirst().get();
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
