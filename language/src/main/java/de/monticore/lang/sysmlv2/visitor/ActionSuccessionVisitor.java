package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;

import java.util.ArrayList;
import java.util.List;

public class ActionSuccessionVisitor implements SysMLActionsVisitor2 {

  @Override
  public void visit(ASTSysMLSuccession node) {
    if(!node.isPresentSrc()) {
      List<ASTSysMLElement> elementList = getElementsofParent(node.getEnclosingScope().getAstNode());
      int index = elementList.indexOf(node);
      for (int i = index - 1; i >= 0; i--) {
        ASTSysMLElement element = elementList.get(i);
        if(element instanceof ASTActionUsage) {
          node.setSrc(((ASTActionUsage) element).getName());
          break;
        }
        if(element instanceof ASTSysMLFirst) {
          node.setSrc(((ASTSysMLFirst) element).getName());
          break;
        }
        if(element instanceof ASTStateUsage) {
          node.setSrc(((ASTStateUsage) element).getName());
          break;
        }
      }
      if(!node.isPresentSrc()) { //TODO soll eventuell geandert werden, ob das durch CoCos gesetzt werden soll

        node.setSrc("start");
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

}
