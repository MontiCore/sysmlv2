package de.monticore.lang.sysmlv2.typecheck;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedName;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisHandler;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisTraverser;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;

import java.util.Optional;

public class DeriveSymTypeofSysMLBasis implements SysMLBasisVisitor2, SysMLBasisHandler {
  protected TypeCheckResult typeCheckResult;
  protected SysMLBasisTraverser traverser;

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  @Override
  public SysMLBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(SysMLBasisTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * 1. Resolves SysMLQualifiedName as OOType symbol.
   * 2. Resolves & sets SymTypeExpression for the resolved OOType symbol as current result.
   *
   * @param node ASTSysMLQualifiedName
   */
  @Override
  public void traverse(ASTSysMLQualifiedName node) {
    Optional<OOTypeSymbol> ooSym = node.getEnclosingScope().resolveOOType(node.getQName());
    if (ooSym.isPresent()) {
      this.typeCheckResult.setType();
      this.typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeExpression(ooSym.get()));
    }
  }
}
