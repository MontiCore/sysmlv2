package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._symboltable.ActionUsageSymbol;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransitionBuilder;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionSuccessionVisitor implements SysMLActionsVisitor2 {

  @Override
  public void visit(ASTSysMLSuccession node) {
    ASTNode parent = node.getEnclosingScope().getAstNode();

    if(parent instanceof ASTActionUsage || parent instanceof ASTActionDef) {
      ASTSysMLElement target = resolveTarget(node);
      boolean srcSet = false;
      if(!node.isPresentSrc()) {
        List<ASTSysMLElement> elementList = getElementsofParent(node.getEnclosingScope().getAstNode());
        int index = elementList.indexOf(node);
        for (int i = index - 1; i >= 0; i--) {
          ASTSysMLElement element = elementList.get(i);
          if(element instanceof ASTActionUsage && (target instanceof ASTActionUsage || (target == null
              && node.getTgt().equals("done")))) {
            node.setSrc(((ASTActionUsage) element).getName());
            srcSet = true;
            break;
          }
          if(element instanceof ASTSysMLFirst && (target instanceof ASTActionUsage || (target == null
              && node.getTgt().equals("done")))) {
            node.setSrc(((ASTSysMLFirst) element).getName());
            srcSet = true;
            break;
          }
        }
        if(!node.isPresentSrc()
            && !srcSet) {
          Log.error("Could not resolve source of the succession with target "+ node.getTgt());
        }
      }
    }
    else if(parent instanceof ASTStateUsage || parent instanceof ASTStateDef) {
      //succession is a transition
      ASTSysMLElement target = resolveTarget(node);
      boolean srcSet = false;
      if(!node.isPresentSrc()) {
        List<ASTSysMLElement> elementList = getElementsofParent(node.getEnclosingScope().getAstNode());
        int index = elementList.indexOf(node);
        for (int i = index - 1; i >= 0; i--) {
          ASTSysMLElement element = elementList.get(i);
          if(element instanceof ASTStateUsage  && (target instanceof ASTStateUsage || (target == null
              && node.getTgt().equals("done")))) {
            //Der Typ muss manuell von succession zu transition geändert werden
            // , da die erkannten wörter von succession und transition nicht diskunkt sind
            elementList.set(index, createTransition(((ASTStateUsage) element).getName(), node));
            srcSet = true;
            break;
          }
        }
        if(!node.isPresentSrc()
            && !srcSet) {
          if(target instanceof ASTStateUsage) {
            //Der Typ muss manuell von succession zu transition geändert werden
            // , da die erkannten wörter von succession und transition nicht diskunkt sind
            elementList.set(index, createTransition("start", node));
          }
          else {
            Log.error("Could not resolve source of the succession.");
          }
        }
      }
      else {
        List<ASTSysMLElement> elementList = getElementsofParent(node.getEnclosingScope().getAstNode());
        int index = elementList.indexOf(node);
        elementList.set(index, createTransition(node.getSrc(), node));
      }
    }
  }

  private ASTSysMLTransition createTransition(String source, ASTSysMLSuccession node) {

    ASTSysMLTransitionBuilder transitionBuilder = new ASTSysMLTransitionBuilder().setTgt(
        node.getTgt()).setSrc(source);

    if(node.isPresentGuard()) {
      transitionBuilder.setGuard(node.getGuard());
    }
    if(node.isPresentInlineAcceptActionUsage()) {
      transitionBuilder.setInlineAcceptActionUsage(node.getInlineAcceptActionUsage());
    }
    ASTSysMLTransition transition = transitionBuilder.build();
    transition.setEnclosingScope(node.getEnclosingScope());
    return transition;
  }

  List<ASTSysMLElement> getElementsofParent(ASTNode astNode) {
    if(astNode instanceof ASTActionDef) {
      return ((ASTActionDef) astNode).getSysMLElementList();

    }
    else if(astNode instanceof ASTActionUsage) {
      return ((ASTActionUsage) astNode).getSysMLElementList();
    }
    else if(astNode instanceof ASTStateDef) {
      return ((ASTStateDef) astNode).getSysMLElementList();
    }
    else if(astNode instanceof ASTStateUsage) {
      return ((ASTStateUsage) astNode).getSysMLElementList();
    }
    return new ArrayList<>();
  }

  ASTSysMLElement resolveTarget(ASTSysMLSuccession node) {
    boolean stateTgtPresent = false;
    boolean actionTgtPresent = false;
    Optional<StateUsageSymbol> stateUsageSymbol = Optional.empty();

    Optional<ActionUsageSymbol> actionUsageSymbol;
    if(node.getEnclosingScope() instanceof ISysMLStatesScope) {
      stateUsageSymbol = ((ISysMLStatesScope) node.getEnclosingScope()).resolveStateUsage(node.getTgt());
      stateTgtPresent = stateUsageSymbol.isPresent();
    }

    actionUsageSymbol = (node.getEnclosingScope()).resolveActionUsage(node.getTgt());
    actionTgtPresent = actionUsageSymbol.isPresent();

    if(stateTgtPresent && actionTgtPresent) {
      Log.error("Could not resolve the target " + node.getTgt()
          + " of a succession, a state usage and an action usage with this name has been found."
      );
    }
    if(stateTgtPresent) {
      return stateUsageSymbol.get().getAstNode();
    }
    if(actionTgtPresent) {
      return actionUsageSymbol.get().getAstNode();
    }
    return null;
  }
}
