package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTDoAction;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;

import java.util.ArrayList;
import java.util.List;

public class TransitionVisitor implements SysMLStatesVisitor2 {

  @Override
  public void visit(ASTSysMLTransition node) {
    if(!node.isPresentSrc()) {
      List<ASTSysMLElement> elementList = getElementsofParent(node.getEnclosingScope().getAstNode());
      int index = elementList.indexOf(node);
      if(index != 0) {
        for (int i = index - 1; i >= 0; i--) {
          ASTSysMLElement element = elementList.get(i);
          if(element instanceof ASTStateUsage) {
            //Der Typ muss manuell von succession zu transition geändert werden
            // , da die erkannten wörter von succession und transition nicht diskunkt sind
            node.setSrc(((ASTStateUsage) element).getName());
            break;
          }
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
