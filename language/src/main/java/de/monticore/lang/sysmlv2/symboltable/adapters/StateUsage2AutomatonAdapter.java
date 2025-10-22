package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.componentconnector._ast.ASTConfiguration;
import de.monticore.lang.componentconnector._ast.ASTEventTransition;
import de.monticore.lang.componentconnector._ast.ASTStateSpace;
import de.monticore.lang.componentconnector._ast.ASTTransition;
import de.monticore.lang.componentconnector._symboltable.AutomatonSymbol;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class StateUsage2AutomatonAdapter extends AutomatonSymbol {
  private final StateUsageSymbol adaptee;

  private final PartDefSymbol container;

  /** Caching because of internal usage of deriver */
  private final List<ASTConfiguration> initialConfiguration = new ArrayList<>();

  public StateUsage2AutomatonAdapter(
      PartDefSymbol container,
      StateUsageSymbol adaptee)
  {
    super(container.getName());
    this.container = container;
    this.adaptee = adaptee;

    stateSpace = new StateSpaceWrapper(adaptee);

    if (adaptee.isPresentAstNode()) {
      var ast = adaptee.getAstNode();

      if(ast.isPresentEntryAction()
          && ast.getSysMLElement(0) instanceof ASTSysMLSuccession)
      {
        var entry = ast.getEntryAction();
        var initialState = ((ASTSysMLSuccession) ast.getSysMLElement(0))
            .getSuccessionThen().getMCQualifiedName().getQName();

        if(entry.isPresentActionUsage()) {
          initialConfiguration.add(
              // Wir gehen aktuell davon aus, dass "Automaton" genau TSYN-Aut.
              // sind. Nur EventAutomaten können also Listen senden.
              new ConfigurationWrapper(
                  initialState,
                  entry.getActionUsage(),
                  false));
        }
        else {
          initialConfiguration.add(
              new ConfigurationWrapper(initialState,
                  (ISysMLv2Scope) entry.getEnclosingScope()));
        }
      }

      transitions = ast.getSysMLElementList().stream()
          .filter(t -> t instanceof ASTSysMLTransition)
          // Wir gehen davon aus, dass die aktuelle Impl. nur Tsyn- und Event-
          // automaten kennt. Tsyn kann keine Listen senden, deswegen "false"
          .map(t -> new TransitionWrapper((ASTSysMLTransition) t, false))
          .collect(Collectors.toList());
    }
  }

  public StateUsageSymbol getAdaptee() {
    return adaptee;
  }

  public PartDefSymbol getContainer() {
    return container;
  }

  @Override
  public String getFullName() {
    return getContainer().getFullName();
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) getAdaptee().getEnclosingScope();
  }

  @Override
  public List<ASTConfiguration> getInitialConfigurationList() {
    return initialConfiguration;
  }

  private String getTrigger(ASTSysMLTransition transition) {
    var trigger = transition.getInlineAcceptActionUsage();

    return String.join(".", ((ASTMCQualifiedType)trigger.getPayload()
        .getPayloadType()).getMCQualifiedName().getPartsList());
  }
}
