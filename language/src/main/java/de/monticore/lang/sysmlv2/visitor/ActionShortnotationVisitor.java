package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._symboltable.ActionUsageSymbol;
import de.monticore.lang.sysmlactions._symboltable.ActionUsageSymbolBuilder;
import de.monticore.lang.sysmlactions._symboltable.ISysMLActionsScope;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ActionShortnotationVisitor implements SysMLActionsVisitor2 {

  @Override
  public void visit(ASTSysMLSuccession node) {
    if(!node.isPresentTgt()) {
      if(node.isPresentActionUsage()) {
        var actionUsage = node.getActionUsage();
        if(actionUsage.isPresentName()) {
          node.setTgt(actionUsage.getName());
          addElementToParent(actionUsage, node.getEnclosingScope(),
              getElementsofParent(node.getEnclosingScope().getAstNode()).indexOf(node));
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
            addElementToParent(actionUsage, node.getEnclosingScope(),
                getElementsofParent(node.getEnclosingScope().getAstNode()).indexOf(node));
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

  void addElementToParent(ASTActionUsage element, ISysMLActionsScope scope, int index) {
    ActionUsageSymbol usageSymbol = new ActionUsageSymbolBuilder().setName(element.getName()).setAstNode(
        element).setEnclosingScope(scope).build();
    element.setSymbol(usageSymbol);
    scope.add(element.getSymbol());
    var parent = scope.getAstNode();
    if(parent instanceof ASTActionUsage) {
      ((ASTActionUsage) parent).getSpannedScope().add(element.getSymbol());
      ((ASTActionUsage) parent).getSysMLElementList().add(index+1, element);
    }
    if(parent instanceof ASTActionDef) {
      ((ASTActionDef) parent).getSpannedScope().add(element.getSymbol());
      ((ASTActionDef) parent).getSysMLElementList().add(index+1, element);
    }
  }
}
