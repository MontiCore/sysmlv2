package de.monticore.lang.sysmlv2.visitors;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._visitor.SysMLActionsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Findet alle ASTSendActionNodeDeclaration und baut eine Liste der Assignments. Hilft die verschiedenen SysML-
 * Möglichkeiten ("send { a to b; b to c; }", "send a to b") abzugrasen.
 */
public class SendActionAssignmentsVisitor implements SysMLActionsVisitor2 {

  /** Returns Pairs of ["<port-name>", "<output-expression>"] for each assigned port. Does not include "silent" ports. */
  private final Map<ASTMCQualifiedName, ASTExpression> assignments  = new LinkedHashMap<>();

  public Map<ASTMCQualifiedName, ASTExpression> getAssignments() {
    return assignments;
  }

  @Override
  public void visit(ASTSendActionUsage node) {
  var target = node.getTarget();
  var value = node.getPayload();
  assignments.put(target, value);
  }

  @Override
  public void visit(ASTAssignmentActionUsage node) {
  assignments.put(node.getTarget(), node.getValueExpression());
  }
}
