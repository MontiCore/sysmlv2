package de.monticore.lang.sysmlv2.typecheck;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlbasis._symboltable.SysMLUsageSymbol;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTPartProperty;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLPortUsage;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsHandler;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsTraverser;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementSubject;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlstatemachinediagrams._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class DeriveSymTypeOfSysMLExpression extends DeriveSymTypeOfExpression
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
   * We first attempt to get type via call to method of super class and, if that fails,
   * then it resolves for SysMLUsage and subsequently sets the type of name expression
   * by extracting it from the definition of the usage, if found.
   *
   * @param node ASTNameExpression
   * @return Optional<SymTypeExpression>
   */
  public Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression node) {
    super.calculateNameExpression(node);
    if (!typeCheckResult.isPresentCurrentResult()) {
      ASTMCQualifiedType mcType = null;
      ASTExpression exp = null;

      Optional<SysMLUsageSymbol> sym = ((SysMLv2Scope) node.getEnclosingScope()).resolveSysMLUsage(node.getName());
      if (sym.isPresent()) {

        // If this is a part property, then look for either its type or an expression.
        if (sym.get().getAstNode() instanceof ASTPartProperty) {
          if (((ASTPartProperty) sym.get().getAstNode()).getSysMLPropertyModifier().sizeTypes() > 0) {
            mcType = (ASTMCQualifiedType) ((ASTPartProperty) sym.get().getAstNode()).getSysMLPropertyModifier().getTypes(0);
          } else if (((ASTPartProperty) sym.get().getAstNode()).getSysMLPropertyModifier().sizeExpressions() > 0) {
            exp = ((ASTPartProperty) sym.get().getAstNode()).getSysMLPropertyModifier().getExpression(0);
          }
        }

        // If this is a port usage, then only look for its type.
        else if (sym.get().getAstNode() instanceof ASTSysMLPortUsage) {
          if (((ASTSysMLPortUsage) sym.get().getAstNode()).isPresentMCType()) {
            mcType = (ASTMCQualifiedType) ((ASTSysMLPortUsage) sym.get().getAstNode()).getMCType();
          }
        }

        // If this is a constraint usage, then only look for its type.
        else if (sym.get().getAstNode() instanceof ASTConstraintUsage) {
          if (((ASTConstraintUsage) sym.get().getAstNode()).isPresentMCType()) {
            mcType = (ASTMCQualifiedType) ((ASTConstraintUsage) sym.get().getAstNode()).getMCType();
          }
        }

        // If this is a requirement usage, then only look for its type.
        else if (sym.get().getAstNode() instanceof ASTRequirementUsage) {
          if (((ASTRequirementUsage) sym.get().getAstNode()).isPresentMCType()) {
            mcType = (ASTMCQualifiedType) ((ASTRequirementUsage) sym.get().getAstNode()).getMCType();
          }
        }

        // If this is a requirement subject, then look for either its type or an expression.
        else if (sym.get().getAstNode() instanceof ASTRequirementSubject) {
          if (((ASTRequirementSubject) sym.get().getAstNode()).isPresentMCType()) {
            mcType = (ASTMCQualifiedType) ((ASTRequirementSubject) sym.get().getAstNode()).getMCType();
          } else if (((ASTRequirementSubject) sym.get().getAstNode()).isPresentBinding()) {
            exp = ((ASTRequirementSubject) sym.get().getAstNode()).getBinding();
          }
        }

        // If this is a state usage, then only look for its type.
        else if (sym.get().getAstNode() instanceof ASTStateUsage) {
          if (((ASTStateUsage) sym.get().getAstNode()).isPresentMCType()) {
            mcType = (ASTMCQualifiedType) ((ASTStateUsage) sym.get().getAstNode()).getMCType();
          }
        }

        // Otherwise, we just log that the type is not supported yet. This will eventually lead to an error.
        else {
          Log.info("Found SysMLUsage symbol does not fall into supported types.", "DeriveSymTypeOfSysMLExpression");
        }

        if (mcType != null) {
          Optional<TypeSymbol> typeSym = ((SysMLv2Scope) node.getEnclosingScope()).resolveType(mcType.getMCQualifiedName().getQName());
          typeSym.ifPresent(typeSymbol -> this.typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeExpression(typeSymbol)));
          if (typeSym.isPresent()) {
            typeCheckResult.setField();
            return Optional.of(SymTypeExpressionFactory.createTypeExpression(typeSym.get()));
          }
        } else if (exp != null) {
          SymTypeExpression typeSym = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes()).typeOf(exp);
          typeCheckResult.setField();
          return Optional.of(typeSym);
        }
      }
    }
    return Optional.empty();
  }
}
