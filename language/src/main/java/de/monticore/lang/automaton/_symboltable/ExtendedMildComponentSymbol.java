package de.monticore.lang.automaton._symboltable;

import java.util.Optional;

public class ExtendedMildComponentSymbol extends ExtendedMildComponentSymbolTOP {
  public ExtendedMildComponentSymbol(String name) {
    super(name);
  }

  public boolean isStateBased() {
    return getAutomaton().isPresent();
  }

  public Optional<AutomatonSymbol> getAutomaton() {
    return getSpannedScope().getLocalAutomatonSymbols().stream().findFirst();
  }
}
