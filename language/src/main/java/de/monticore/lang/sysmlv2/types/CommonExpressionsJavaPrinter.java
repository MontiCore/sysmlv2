package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisFullPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class CommonExpressionsJavaPrinter extends ExpressionsBasisFullPrettyPrinter {
  protected SysMLv2Traverser traverser = SysMLv2Mill.traverser();

  public SysMLv2Traverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(SysMLv2Traverser traverser) {
    this.traverser = traverser;
  }

  public CommonExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    this.traverser.setCommonExpressionsHandler(commonExpressions);
    this.traverser.add4CommonExpressions(commonExpressions);
    ExpressionsBasisPrettyPrinter basicExpression = new ExpressionsBasisPrettyPrinter(printer);
    this.traverser.setExpressionsBasisHandler(basicExpression);
    this.traverser.add4ExpressionsBasis(basicExpression);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    this.traverser.add4MCBasics(basic);
    MCCommonLiteralsPrettyPrinter commonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    this.traverser.add4MCCommonLiterals(commonLiteralsPrettyPrinter);
    this.traverser.setMCCommonLiteralsHandler(commonLiteralsPrettyPrinter);
  }
}
