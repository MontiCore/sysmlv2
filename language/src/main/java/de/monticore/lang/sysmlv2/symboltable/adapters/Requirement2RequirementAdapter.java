package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlconstraints._ast.ASTSysMLConstraintsNode;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementUsageSymbol;
import de.monticore.lang.sysmlconstraints._visitor.SysMLConstraintsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class Requirement2RequirementAdapter extends RequirementSymbol {

  private RequirementUsageSymbol adaptee;

  public Requirement2RequirementAdapter(RequirementUsageSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.subject =

    var finder = new ConstraintExpressionFinder();
    var traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLConstraints(finder);
    if(adaptee.isPresentAstNode()) {
      adaptee.getAstNode().accept(traverser);
      if(finder.constraint.isPresent()) {
        Log.error("0xF0C07 Internal error, could not find constraint within "
            + "requirement symbol");
      }
      this.constraint = finder.constraint.orElse(null);
    }
    else {
      Log.error("0xF0C08 Internal error, requirement symbol had no AST");
    }
  }

  /**
   * Interner Visitor, die nach ConstraintUsages sucht und sich die Expression
   * der letzten ConstraintUsage merkt.
   */
  class ConstraintExpressionFinder implements SysMLConstraintsVisitor2 {
    Optional<ASTExpression> constraint =  Optional.empty();

    @Override
    public void visit(ASTConstraintUsage node) {
      constraint = Optional.ofNullable(node.getExpression());
    }
  }

}
