package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.componentconnector._symboltable.EventAutomatonSymbol;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * All transitions in an Event-Automaton need AcceptActions
 */
public class EventTransitionRequiresAccept implements
    SysMLStatesASTSysMLTransitionCoCo {

  @Override
  public void check(ASTSysMLTransition node) {
    if (!isInEventAutomaton(node)) return;

    if (!node.isPresentInlineAcceptActionUsage()) {
      Log.error("EventAutomaton transition must contain an AcceptAction.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }

  private boolean isInEventAutomaton(ASTSysMLTransition node) {
    var scope = node.getEnclosingScope();

    while (scope != null) {
      if (scope.isPresentSpanningSymbol()) {
        var sym = scope.getSpanningSymbol();
        if (sym instanceof EventAutomatonSymbol) return true;
      }
      scope = scope.getEnclosingScope();
    }
    return false;
  }

}
