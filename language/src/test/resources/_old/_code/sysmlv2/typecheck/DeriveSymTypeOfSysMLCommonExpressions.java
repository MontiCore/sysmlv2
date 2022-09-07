package de.monticore.lang.sysmlv2.typecheck;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.SymTypeExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DeriveSymTypeOfSysMLCommonExpressions extends DeriveSymTypeOfCommonExpressions {

  /**
   * We search for the field symbols using breadth-first-search
   * to find the field from the super types, if not found in subtype.
   */
  @Override
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    List<VariableSymbol> fieldSymbols = null;
    // Do breadth-first-search to find fields from super types
    // BFS - start
    SymTypeExpression currentNode = innerResult;
    Queue<SymTypeExpression> queue = new LinkedList<>();
    while (currentNode != null) {

      // 1. examine current node
      fieldSymbols = currentNode.getFieldList(expr.getName(), typeCheckResult.isType(), true);
      if (!fieldSymbols.isEmpty()) {
        // break from BFS if field symbols were found
        break;
      }

      // 2. enqueue all super types
      queue.addAll(currentNode.getTypeInfo().getSuperTypesList());

      // 3. dequeue node from queue
      currentNode = queue.poll();
    }
    // BFS - end
    return fieldSymbols;
  }
}
