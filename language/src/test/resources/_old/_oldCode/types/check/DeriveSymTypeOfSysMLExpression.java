package schrott.types.check;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._ast.ASTFeatureReferenceExpression;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.types.check.AbstractDeriveFromExpression;

/**
 * Calculates type of SysMLExpressions. Only supported expression is 'feature reference expression'.
 */
public class DeriveSymTypeOfSysMLExpression extends AbstractDeriveFromExpression
    implements SysMLExpressionsVisitor2, SysMLExpressionsHandler {

  protected SysMLExpressionsTraverser traverser;

  @Override
  public SysMLExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(SysMLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Calculates the type of feature reference expression.
   * Tries to resolve the expression as a function or a variable symbol.
   *
   * @param expr feature reference expression that should ultimately resolve in variable or function symbol
   * @return Optional<SymTypeExpression> type of the resolved symbol
   */
  @Override
  public void traverse(ASTFeatureReferenceExpression expr) {
    // TODO SLE
    //calculateFeatureReferenceExpression(expr);
  }

  // TODO SLE: Diese Methode ist schrecklich und falsch implementiert
  /*private Optional<SymTypeExpression> calculateFeatureReferenceExpression(ASTFeatureReferenceExpression expr) {
    List<String> sysMLNames = expr.getFeatureReference(0).getMemberFeature().getNamesList();

    Optional<SysMLTypeSymbol> symbol = expr.getEnclosingScope().resolveSysMLType(
        String.join(".", sysMLNames.subList(0, sysMLNames.size() - 1)));
    if (symbol.isPresent()) {
      String last = sysMLNames.get(sysMLNames.size() - 1);
      ISysML4VerificationScope searchScope = (ISysML4VerificationScope) symbol.get().getEnclosingScope();

      // if last resolved symbol is a port member, then try resolving 'last' as a function symbol
      if (symbol.get().isPresentAstNode() && symbol.get().getAstNode() instanceof ASTPortMember) {
        ASTPortUsageStd portMemberUsage = (ASTPortUsageStd) ((ASTPortMember) symbol.get().getAstNode()).getPortUsage();
        ASTSysMLNameAndTypePart typePart = ((ASTUsageStd) portMemberUsage.getUsage()).getUsageDeclaration().getSysMLNameAndTypePart();
        ASTFeatureTyping featureTyping = ((ASTTypePartStd) typePart.getTypePart()).getFeatureTyping(0);
        ASTMCQualifiedType portMemberType = (ASTMCQualifiedType) ((ASTFeatureTypingVerification) featureTyping).getMCType();
        Optional<PortDefinitionStdSymbol> port = searchScope.resolvePortDefinitionStd(
            portMemberType.getNameList().get(0));

        if (port.isPresent()) {
          List<FunctionSymbol> functions = port.get().getType().getMethodList(last, false);
          if (functions.size() == 1) {
            typeCheckResult.setCurrentResult(SymTypeExpressionFactoryEx.createTypeExpression(functions.get(0)));
          }
        }
      }
      // otherwise we try to resolve it as a variable symbol
      else {
        Optional<VariableSymbol> var = searchScope.resolveVariable(last);
        if (var.isPresent()) {
          // TODO: maybe do something with it later
        }
      }
    }

    SymTypeExpression result = null;
    if (typeCheckResult.isPresentCurrentResult()) {
      result = typeCheckResult.getCurrentResult();
    }
    else {
      typeCheckResult.reset();
      logError("0xA0174", expr.get_SourcePositionStart());
    }
    return Optional.ofNullable(result);
  }*/

}
