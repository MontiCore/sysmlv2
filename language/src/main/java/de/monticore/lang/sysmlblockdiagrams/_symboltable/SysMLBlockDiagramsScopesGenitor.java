package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.lang.sysmlblockdiagrams.SysMLBlockDiagramsMill;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLAttribute;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SysMLBlockDiagramsScopesGenitor extends SysMLBlockDiagramsScopesGenitorTOP {

  private int counterConnectionUsage = 1;

  private int counterPartProperty = 1;

  private int counterConnectionDefEnd = 1;

  /**
   * Method overridden to set type in field symbol (original method only creates field symbol with name).
   */
  @Override
  public void visit(ASTSysMLAttribute node) {
    FieldSymbol symbol = SysMLBlockDiagramsMill.fieldSymbolBuilder()
            .setName(node.getName())
            .build();

    // Extract type info. from the node and resolve for type symbol.
    Optional<TypeSymbol> typeSymbol = Optional.empty();
    if (node.getSysMLPropertyModifier().sizeTypes() > 0) {
      String typeName = node.getSysMLPropertyModifier().getTypes(0)
              .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
      typeSymbol = SysMLv2Mill.globalScope().resolveType(typeName);
    }

    typeSymbol.ifPresent(value -> symbol.setType(SymTypeExpressionFactory.createTypeExpression(value)));

    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xA5021x25703 Symbol cannot be added to current scope, since no scope exists.");
    }
    // symbol -> ast
    symbol.setAstNode(node);

    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());

    initFieldHP1(node.getSymbol());
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionUsage node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConncectionUsage" + counterConnectionUsage);
      counterConnectionUsage += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTPartProperty node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed PartProperty" + counterPartProperty);
      counterPartProperty += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionDefEnd node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConnectionDefEnd" + counterConnectionDefEnd);
      counterConnectionDefEnd += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }
}
