package de.monticore.lang.sysmlv2.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlactions._ast.ASTSysMLSuccession;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;

import java.util.ArrayList;
import java.util.List;

public class ActionSuccessionVisitor implements SysMLActionsVisitor2 {

  @Override
  public void visit(ASTSysMLSuccession node) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(!node.isPresentSrc()) {
      ASTNode astNode = node.getEnclosingScope().getAstNode();
      if(astNode instanceof ASTActionDef) {
        elementList = ((ASTActionDef) astNode).getSysMLElementList();

      }
      else if(astNode instanceof ASTActionUsage) {
        elementList = ((ASTActionUsage) astNode).getSysMLElementList();
      } //TODO force that successions are children of action def or usage
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

      }
    }

  }
}
