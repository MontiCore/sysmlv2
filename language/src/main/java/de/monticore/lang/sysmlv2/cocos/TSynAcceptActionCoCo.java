package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Transitions in a #tsyn automaton don't need an AcceptAction. Will still
 * be parsed but throws a warning and the AcceptAction will be ignored.
 */

public class TSynAcceptActionCoCo
    implements SysMLStatesASTSysMLTransitionCoCo {

  @Override
  public void check(ASTSysMLTransition node) {
    if(isInTsynAutomaton(node)) {
      if (node.isPresentInlineAcceptActionUsage()) {
        Log.warn(
          "Time-synchronous states (annotated with #tsyn), "
              + "can not react on an event-driven basis. "
              + "The keyword \"accept\" on transitions will be ignored.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
      }
    }
  }

  private boolean isInTsynAutomaton(ASTSysMLTransition node) {
    var scope = node.getEnclosingScope();
    while (scope != null) {
      if (scope.isPresentSpanningSymbol()) {
        var sym = scope.getSpanningSymbol();

        if (sym.isPresentAstNode() && sym.getAstNode() instanceof ASTStateUsage) {
          ASTStateUsage stateUsage = (ASTStateUsage) sym.getAstNode();
          if (hasTsynKeyword(stateUsage)) {
            return true;
          }
        }
      }
      scope = scope.getEnclosingScope();
  }
  return false;
  }

  private boolean hasTsynKeyword(ASTStateUsage stateUsage) {
    return stateUsage.getUserDefinedKeywordList().stream()
        .anyMatch(k ->
            k.getMCQualifiedName().getBaseName().equals("tsyn"));
  }
}

