package de.monticore.lang.sysml4verification.symboltable;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintDef;
import de.monticore.lang.sysmlparametrics._visitor.SysMLParametricsVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;

/**
 * Wir nutzen einen ConstraintVisitor, um nach der Scopegenerierung durch den Genitor, die Symboltable
 * mit den Types für Parameter zu füllen.
 * Nimmt an, dass der Type existiert. Es sollte vor dem STCompleter die CoCo `ConstraintParameterTypeIsValidCoCo`
 * ausgeführt werden
 */
public class ConstraintDefinitionSymbolTableCompleter implements SysMLParametricsVisitor2 {

  private final MCSimpleGenericTypesFullPrettyPrinter typePrinter = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();

  /**
   * Nachdem wir die ConstraintDefDeclaration besucht haben (mit visit(ASTConstraintDefDeclaration node)),
   * sind die Scopes der Parameter gesetzt und die Symbole in der Symboltable.
   * Bei endVisit können wir dann die Types von den Symbolen ergänzen, sofern es überhaupt Parameter gibt.
   */
  @Override
  public void endVisit(ASTConstraintDef node) {
      node.getParameterList().streamSysMLParameters()
          .forEach(parameterAST -> {
            FieldSymbol parameterSymbol = parameterAST.getSymbol();

            parameterSymbol.setIsReadOnly(false);
            ASTMCType parameterType = parameterAST.getMCType();
            // `createTypeExpression` nimmt an, dass der Type existiert - `ConstraintParameterTypeIsValidCoCo` vorher!
            parameterSymbol.setType(
                SymTypeExpressionFactory.createTypeExpression(
                    parameterType.printType(typePrinter),
                    parameterAST.getEnclosingScope()));
            parameterSymbol.setIsReadOnly(true);
          });
  }
}
