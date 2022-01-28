package de.monticore.lang.sysmlcommons._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;

/**
 * ASTSysMLParameter was extended for computation and easy access to the parameter type
 * and assigned expression. It is, as of now, used by requirements to validate type
 * compatibility between the parameter type and its assigned value.
 */
public class ASTSysMLParameter extends ASTSysMLParameterTOP {
  private SymTypeExpression type;

  private ASTExpression expression;

  private boolean typeInferred;

  public SymTypeExpression getType() {
    return type;
  }

  public void setType(SymTypeExpression type) {
    this.type = type;
  }

  public ASTExpression getExpression() {
    return expression;
  }

  public void setExpression(ASTExpression expression) {
    this.expression = expression;
  }

  public boolean isTypeInferred() {
    return typeInferred;
  }

  public void setTypeInferred(boolean typeInferred) {
    this.typeInferred = typeInferred;
  }

  /**
   * Compares the parameter's type and expression with the given parameter for equality.
   *
   * @param other ASTSysMLParameter
   * @return boolean true if equal, false otherwise.
   */
  public boolean isEqual(ASTSysMLParameter other) {
    return ((this.type != null && this.type.deepEquals(other.type)) || (this.type == null && other.type == null))
        && ((this.expression != null && this.expression.deepEquals(other.expression)) || (this.expression == null
        && other.expression == null));
  }
}
