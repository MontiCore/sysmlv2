package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.lang.sysmlblockdiagrams.SysMLBlockDiagramsMill;
import de.se_rwth.commons.logging.Log;

public class SysMLBlockDiagramsScopesGenitor extends SysMLBlockDiagramsScopesGenitorTOP {

  private int counterConnectionUsage = 1;

  private int counterPartProperty = 1;

  private int counterConnectionDefEnd = 1;

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionUsage node) {

    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConncectionUsage" + counterConnectionUsage);
      counterConnectionUsage += 1;
    }

    // The following part is copied from the generated SysMLBlockDiagramsScopesGenitorTOP file
    ConnectionUsageSymbol symbol = SysMLBlockDiagramsMill.connectionUsageSymbolBuilder().setName(
        node.getName()).build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    }
    else {
      Log.warn("0xA5021x05490 Symbol cannot be added to current scope, since no scope exists.");
    }
    ISysMLBlockDiagramsScope scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
    // symbol -> ast
    symbol.setAstNode(node);
    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());
    // scope -> ast
    scope.setAstNode(node);
    // ast -> scope
    node.setSpannedScope(scope);
    initConnectionUsageHP1(node.getSymbol());
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTPartProperty node) {

    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed PartProperty" + counterPartProperty);
      counterPartProperty += 1;
    }

    // The following part is copied from the generated SysMLBlockDiagramsScopesGenitorTOP file
    PartPropertySymbol symbol = SysMLBlockDiagramsMill.partPropertySymbolBuilder().setName(
        node.getName()).build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    }
    else {
      Log.warn("0xA5021x29066 Symbol cannot be added to current scope, since no scope exists.");
    }
    ISysMLBlockDiagramsScope scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
    // symbol -> ast
    symbol.setAstNode(node);
    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());
    // scope -> ast
    scope.setAstNode(node);
    // ast -> scope
    node.setSpannedScope(scope);
    initPartPropertyHP1(node.getSymbol());
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionDefEnd node) {

    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConnectionDefEnd" + counterConnectionDefEnd);
      counterConnectionDefEnd += 1;
    }

    // The following part is copied from the generated SysMLBlockDiagramsScopesGenitorTOP file
    ConnectionDefEndSymbol symbol = SysMLBlockDiagramsMill.connectionDefEndSymbolBuilder().setName(
        node.getName()).build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    }
    else {
      Log.warn("0xA5021x73265 Symbol cannot be added to current scope, since no scope exists.");
    }
    de.monticore.lang.sysmlblockdiagrams._symboltable.ISysMLBlockDiagramsScope scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
    // symbol -> ast
    symbol.setAstNode(node);
    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());
    // scope -> ast
    scope.setAstNode(node);
    // ast -> scope
    node.setSpannedScope(scope);
    initConnectionDefEndHP1(node.getSymbol());
  }
}
