package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlbasis._ast.ASTDefaultValueBuilder;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlstates._symboltable.SysMLStatesScope;
import de.monticore.lang.sysmlstates._visitor.SysMLStatesVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;

import java.util.Optional;
import java.util.stream.Collectors;

public class StateVisitor implements SysMLStatesVisitor2 {

  @Override
  public void visit(ASTStateUsage node) {
    if(node.getSysMLElementList().stream().anyMatch(
        t -> t instanceof ASTStateUsage | t instanceof ASTSysMLTransition)) {
      node.setIsAutomaton(true);
      node.getSysMLElementList().add(createStateUsage("start", node.getSpannedScope()));
      node.getSysMLElementList().add(createStateUsage("done", node.getSpannedScope()));
    }
    else {
      node.setIsAutomaton(isParentAutomaton(node));
    }
  }

  @Override
  public void visit(ASTStateDef node) {
    if(node.getSysMLElementList().stream().anyMatch(
        t -> t instanceof ASTStateUsage | t instanceof ASTSysMLTransition)) {
      node.setIsAutomaton(true);
      node.getSysMLElementList().add(createStateUsage("start", node.getSpannedScope()));
      node.getSysMLElementList().add(createStateUsage("done", node.getSpannedScope()));
    }
    else {
      node.setIsAutomaton(isParentAutomaton(node));
    }
  }

  boolean isNodeAutomaton(ASTSysMLElement node) {
    if(node instanceof ASTStateUsage) {
      if(((ASTStateUsage) node).getSysMLElementList().stream().anyMatch(
          t -> t instanceof ASTStateUsage | t instanceof ASTSysMLTransition)) {
        return true;
      }
      return false;
    }
    if(node instanceof ASTStateDef) {
      if(((ASTStateDef) node).getSysMLElementList().stream().anyMatch(
          t -> t instanceof ASTStateUsage | t instanceof ASTSysMLTransition)) {
        return true;
      }
      return false;
    }
    return false;
  }

  ASTStateUsage createStateUsage(String name, ISysMLStatesScope enclosingScope) {
    ASTModifierBuilder modifierBuilder = new ASTModifierBuilder();
    ASTDefaultValueBuilder defaultValueBuilder = new ASTDefaultValueBuilder();
    StateUsageSymbol stateUsageSymbol = new StateUsageSymbol(name);
    ASTStateUsage stateUsage = new ASTStateUsage();
    stateUsageSymbol.setAstNode(stateUsage);
    stateUsage.setName(name);
    stateUsage.setEnclosingScope(enclosingScope);
    stateUsage.setSpannedScope(new SysMLStatesScope());
    stateUsage.setModifier(modifierBuilder.build());
    stateUsage.setDefaultValue(defaultValueBuilder.build());
    stateUsage.setSymbol(stateUsageSymbol);
    enclosingScope.add(stateUsage.getSymbol());
    return stateUsage;
  }

  boolean isParentAutomaton(ASTSysMLElement element) {
    if(element instanceof ASTStateUsage) {
      if(isNodeAutomaton(element) || ((ASTStateUsage) element).getIsAutomaton())
        return true;
      var stateDefList = ((ASTStateUsage) element).getSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLTyping).flatMap(t -> t.streamSuperTypes()).map(
          t -> ((ASTStateUsage) element).getEnclosingScope().resolveStateDef(printName(t))).filter(
          Optional::isPresent
      ).map(t -> t.get().getAstNode()).collect(Collectors.toList());
      var stateUsageList = ((ASTStateUsage) element).getSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(t -> t.streamSuperTypes()).map(
          t -> ((ASTStateUsage) element).getEnclosingScope().resolveStateUsage(printName(t))).filter(
          Optional::isPresent
      ).map(t -> t.get().getAstNode()).collect(Collectors.toList());
      if(stateUsageList.isEmpty() && stateDefList.isEmpty())
        return false;

      var stateDefParent = stateDefList.stream().anyMatch(this::isParentAutomaton);
      var stateUsageParent = stateUsageList.stream().anyMatch(this::isParentAutomaton);
      return stateDefParent || stateUsageParent;
    }
    if(element instanceof ASTStateDef) {
      if(isNodeAutomaton(element) || ((ASTStateDef) element).getIsAutomaton())
        return true;
      var stateDefList = ((ASTStateDef) element).getSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(t -> t.streamSuperTypes()).map(
          t -> ((ASTStateDef) element).getEnclosingScope().resolveStateDef(printName(t))).filter(
          Optional::isPresent
      ).map(t -> t.get().getAstNode()).collect(Collectors.toList());
      var stateDefParent = stateDefList.stream().anyMatch(this::isParentAutomaton);
      return stateDefParent;
    }
    return false;
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
