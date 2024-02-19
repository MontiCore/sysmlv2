package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.automaton._ast.ASTConfiguration;
import de.monticore.lang.automaton._ast.ASTEventTransition;
import de.monticore.lang.automaton._ast.ASTStateSpace;
import de.monticore.lang.automaton._ast.ASTTransition;
import de.monticore.lang.automaton._symboltable.AutomatonSymbol;
import de.monticore.lang.automaton._symboltable.IAutomatonScope;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StateUsage2AutomatonAdapter extends AutomatonSymbol {
  private final StateUsageSymbol adaptee;

  private final PartDefSymbol container;

  /** Caching because of internal usage of deriver */
  private final List<ASTConfiguration> initialConfiguration = new ArrayList<>();

  public StateUsage2AutomatonAdapter(PartDefSymbol container, StateUsageSymbol adaptee) {
    super(container.getName());
    this.container = container;
    this.adaptee = adaptee;

    stateSpace = new StateSpaceWrapper(adaptee);

    if (adaptee.isPresentAstNode()) {
      var ast = adaptee.getAstNode();

      if(ast.isPresentEntryAction() && ast.getSysMLElement(0) instanceof ASTSysMLSuccession) {


        var entry = ast.getEntryAction();
        var initialState = ((ASTSysMLSuccession) ast.getSysMLElement(0)).getTgt();

        if(entry.isPresentActionUsage()) {
          initialConfiguration.add(new ConfigurationWrapper(initialState, entry.getActionUsage()));
        }
        else {
          initialConfiguration.add(new ConfigurationWrapper(initialState));
        }
      }

      Map<String,List<ImmutablePair<ASTSysMLTransition, ASTTransition>>> transitions = ast.getSysMLElementList()
          .stream()
          .filter(elem -> elem instanceof ASTSysMLTransition)
          .map(trans -> new ImmutablePair<ASTSysMLTransition, ASTTransition>(
              (ASTSysMLTransition) trans,
              new TransitionWrapper((ASTSysMLTransition) trans))
          )
          .collect(Collectors.groupingBy(transPair -> getTrigger(transPair.getLeft())));

      eventTransitions = transitions
          .entrySet()
          .stream()
          .filter(entry -> !entry.getKey().equals("Tick"))
          .map(entry -> new EventTransitionWrapper(
              entry.getKey(),
              entry.getValue().stream().map(ImmutablePair::getRight).collect(Collectors.toList()))
          )
          .collect(Collectors.toList());

      tickTransitions = transitions.getOrDefault("Tick", List.of())
          .stream()
          .map(ImmutablePair::getRight)
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
    //TODO nochmal richtig machen
    var trigger = transition.getInlineAcceptActionUsage();

    return String.join(".", ((ASTMCQualifiedType)trigger.getPayloadType()).getMCQualifiedName().getPartsList());
  }
}
