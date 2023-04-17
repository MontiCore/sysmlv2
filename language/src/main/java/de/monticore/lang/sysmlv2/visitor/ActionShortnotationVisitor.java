package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._symboltable.AssignmentActionUsageSymbol;
import de.monticore.lang.sysmlactions._symboltable.AssignmentActionUsageSymbolBuilder;
import de.monticore.lang.sysmlactions._symboltable.SendActionUsageSymbol;
import de.monticore.lang.sysmlactions._symboltable.SendActionUsageSymbolBuilder;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTEntryAction;
import de.monticore.lang.sysmlstates._ast.ASTExitAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionShortnotationVisitor implements SysMLActionsVisitor2, SysMLStatesVisitor2 {

  static HashMap<ASTSysMLElement, List<ASTSysMLElement>> changes = new HashMap<>();
  @Override
  public void visit(ASTDoAction node){
    if(node.isPresentActionUsage()) {
      var actionUsage = node.getActionUsage();
      if(!actionUsage.isPresentName()) {
        if(actionUsage instanceof ASTSendActionUsage || actionUsage instanceof ASTAssignmentActionUsage) {
          int counter = 0;
          String name = "actionShort_";
          boolean nameNotFound = true;
          while (nameNotFound) {
            nameNotFound = node.getEnclosingScope().resolveActionUsage(name + counter).isPresent();
          }
          actionUsage.setName(name + counter);
        }
        else
          Log.error("Could not get the name of the action usage within a do action."
          );
      }
    }
  }
  @Override
  public void visit(ASTEntryAction node){
    if(node.isPresentActionUsage()) {
      var actionUsage = node.getActionUsage();
      if(!actionUsage.isPresentName()) {
        if(actionUsage instanceof ASTSendActionUsage || actionUsage instanceof ASTAssignmentActionUsage) {
          int counter = 0;
          String name = "actionShort_";
          boolean nameNotFound = true;
          while (nameNotFound) {
            nameNotFound = node.getEnclosingScope().resolveActionUsage(name + counter).isPresent();
          }
          actionUsage.setName(name + counter);
        }
        else
          Log.error("Could not get the name of the action usage within an entry action."
          );
      }
    }
  }
  @Override
  public void visit(ASTExitAction node){
    if(node.isPresentActionUsage()) {
      var actionUsage = node.getActionUsage();
      if(!actionUsage.isPresentName()) {
        if(actionUsage instanceof ASTSendActionUsage || actionUsage instanceof ASTAssignmentActionUsage) {
          int counter = 0;
          String name = "actionShort_";
          boolean nameNotFound = true;
          while (nameNotFound) {
            nameNotFound = node.getEnclosingScope().resolveActionUsage(name + counter).isPresent();
          }
          actionUsage.setName(name + counter);
        }
        else
          Log.error("Could not get the name of the action usage within an exit action."
          );
      }
    }
  }

  @Override
  public void visit(ASTSysMLSuccession node) {
    if(!node.isPresentTgt()) {
      if(node.isPresentActionUsage()) {
        if(!changes.containsKey(node)) {
          changes.put((ASTSysMLElement) node.getEnclosingScope().getAstNode(),
              new ArrayList(getElementsOfParent(node.getEnclosingScope().getAstNode())));
        }
        var actionUsage = node.getActionUsage();
        if(actionUsage.isPresentName()) {
          node.setTgt(actionUsage.getName());
          addElementToParent(actionUsage, node);
        }
        else {
          if(actionUsage instanceof ASTSendActionUsage || actionUsage instanceof ASTAssignmentActionUsage) {
            int counter = 0;
            String name = "actionShort_";
            boolean nameNotFound = true;
            while (nameNotFound) {
              nameNotFound = node.getEnclosingScope().resolveActionUsage(name + counter).isPresent();
            }
            node.setTgt(name + counter);
            actionUsage.setName(name + counter);
            addElementToParent(actionUsage, node);
          }
          else
            Log.error("Could not get the name of the action usage within a succession."
            );
        }
      }
      else {
        Log.error("Succession has no target and no short notation action usage."
        );
      }
    }
  }

  List<ASTSysMLElement> getElementsOfParent(ASTNode astNode) {
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

  void addElementToParent(ASTActionUsage element, ASTSysMLSuccession node) {

    if(element instanceof ASTAssignmentActionUsage) {
      AssignmentActionUsageSymbol usageSymbol = (AssignmentActionUsageSymbol) new AssignmentActionUsageSymbolBuilder().setName(
          element.getName()).setAstNode(
          element).setEnclosingScope(node.getEnclosingScope()).build();
      ((ASTAssignmentActionUsage) element).setSymbol(usageSymbol);
    }
    if(element instanceof ASTSendActionUsage) {
      SendActionUsageSymbol usageSymbol = (SendActionUsageSymbol) new SendActionUsageSymbolBuilder().setName(
          element.getName()).setAstNode(
          element).setEnclosingScope(node.getEnclosingScope()).build();
      ((ASTSendActionUsage) element).setSymbol(usageSymbol);
    }

    var parent = node.getEnclosingScope().getAstNode();
    if(parent instanceof ASTActionUsage) {
      //((ASTActionUsage) parent).getSpannedScope().add(element.getSymbol());
      List<ASTSysMLElement> elementList = changes.get(parent);
      int index = elementList.indexOf(node);
      elementList.add(index + 1, element);
    }
    if(parent instanceof ASTActionDef) {
      // ((ASTActionDef) parent).getSpannedScope().add(element.getSymbol());
      List<ASTSysMLElement> elementList = changes.get(parent);
      int index = elementList.indexOf(node);
      elementList.add(index + 1, element);
    }
  }

  public void applyTransformations() {
    for (ASTSysMLElement action : changes.keySet()
    ) {
      if(action instanceof ASTActionUsage) {
        List<ASTSysMLElement> elementList = changes.get(action);
        for (ASTSysMLElement element : elementList) {
          if(element instanceof ASTActionUsage) {
            ((ASTActionUsage) action).getSpannedScope().add(((ASTActionUsage) element).getSymbol());
          }
        }
        ((ASTActionUsage) action).setSysMLElementList(changes.get(action));

      }
      if(action instanceof ASTActionDef) {
        List<ASTSysMLElement> elementList = changes.get(action);
        for (ASTSysMLElement element : elementList) {
          if(element instanceof ASTActionUsage) {
            ((ASTActionDef) action).getSpannedScope().add(((ASTActionUsage) element).getSymbol());
          }
        }
        ((ASTActionDef) action).setSysMLElementList(changes.get(action));
      }

    }

  }
}
