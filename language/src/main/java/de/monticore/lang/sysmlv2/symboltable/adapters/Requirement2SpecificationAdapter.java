package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.componentconnector._symboltable.MildSpecificationSymbol;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Similar to {@link Constraint2SpecificationAdapter}, this wrapper poses as {@link MildSpecificationSymbol}s to the
 * outside. The diffference to the adapter for constraints is, that this wraps requirements instead. The reason is,
 * that according to our understanding, the following two models should express the same thing (i.e., have equal
 * semantics):
 *
 * <code>
 *   assert constraint One { 1+2 == 3 }
 *   satisfy requirement Two { assert constraint One { 1-2 == 3 } }
 * </code>
 *
 * As such, both are adapted to an equally to the outside looking {@link MildSpecificationSymbol}!
 */
public class Requirement2SpecificationAdapter extends MildSpecificationSymbol {

  private RequirementUsageSymbol adaptee;

  public Requirement2SpecificationAdapter(RequirementUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof Requirement2SpecificationAdapter) {
      return adaptee.equals(((Requirement2SpecificationAdapter) other).adaptee);
    }
    else {
      return super.equals(other);
    }
  }

  @Override
  public ISysMLv2Scope getEnclosingScope() {
    return (ISysMLv2Scope) adaptee.getEnclosingScope();
  }

  @Override
  public List<ASTExpression> getAssumptionsList() {
    if(adaptee.isPresentAstNode()) {
      var reqUsage = adaptee.getAstNode();
      var constraints = ((ISysMLv2Scope)adaptee.getSpannedScope()).getLocalConstraintUsageSymbols();
      var assumed = constraints.stream().filter(c -> c.getAstNode().isAssume());
      return assumed.map(c -> c.getAstNode().getExpression()).collect(Collectors.toList());
    }
    else {
      Log.error("0x10003 AST of Requirement Usage not present");
      return new ArrayList<>();
    }
  }

  @Override
  public List<ASTExpression> getPredicatesList() {
    if(adaptee.isPresentAstNode()) {
      var reqUsage = adaptee.getAstNode();
      var constraints = ((ISysMLv2Scope)adaptee.getSpannedScope()).getLocalConstraintUsageSymbols();
      var asserted = constraints.stream().filter(c -> c.getAstNode().isAssert() || c.getAstNode().isRequire());
      return asserted.map(c -> c.getAstNode().getExpression()).collect(Collectors.toList());
    }
    else {
      Log.error("0x10003 AST of Requirement Usage not present");
      return new ArrayList<>();
    }
  }

}
