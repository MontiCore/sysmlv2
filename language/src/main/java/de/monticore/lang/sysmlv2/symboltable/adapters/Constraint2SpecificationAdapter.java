package de.monticore.lang.sysmlv2.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.componentconnector._symboltable.MildSpecificationSymbol;
import de.monticore.lang.sysmlconstraints._symboltable.ConstraintUsageSymbol;
import de.monticore.lang.sysmlrequirements._symboltable.RequirementUsageSymbol;

/**
 * Similar to {@link Requirement2SpecificationAdapter}, this wrapper poses as {@link MildSpecificationSymbol}s to the
 * outside. The diffference to the adapter for requirements is, that this wraps constraints instead. The reason is,
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
public class Constraint2SpecificationAdapter extends MildSpecificationSymbol {

  private ConstraintUsageSymbol adaptee;

  public Constraint2SpecificationAdapter(ConstraintUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof Constraint2SpecificationAdapter) {
      return adaptee.equals(((Constraint2SpecificationAdapter) other).adaptee);
    }
    else {
      return super.equals(other);
    }
  }

}
