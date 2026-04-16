package de.monticore.lang.sysmlparts.symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.lang.sysmlactions._ast.ASTCalcUsage;
import de.monticore.lang.sysmlactions._symboltable.CalcUsageSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.SourcePosition;

public class CalcUsage2FunctionSymbolAdapter extends FunctionSymbol {
  protected CalcUsageSymbol adaptee;

  public CalcUsage2FunctionSymbolAdapter(CalcUsageSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee.getName()));
    this.adaptee = adaptee;

    setAccessModifier(adaptee.getAccessModifier());

    IBasicSymbolsScope spannedScope = BasicSymbolsMill.scope();
    spannedScope.setName(adaptee.getName());
    spannedScope.setShadowing(true);
    spannedScope.setEnclosingScope(adaptee.getEnclosingScope());
    setSpannedScope(spannedScope);

    setType(deriveReturnType());
  }

  protected CalcUsageSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public String getName() {
    return adaptee.getName();
  }

  @Override
  public String getFullName() {
    return adaptee.getFullName();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return adaptee.getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return adaptee.getSourcePosition();
  }

  protected SymTypeExpression deriveReturnType() {
    if (!adaptee.isPresentAstNode()) {
      throw new IllegalStateException("CalcUsageSymbol has no AST node: " + adaptee.getFullName());
    }

    ASTCalcUsage ast = adaptee.getAstNode();

    if (ast.getSysMLElementList().isEmpty()) {
      throw new IllegalStateException("CalcUsage has no body elements: " + adaptee.getFullName());
    }

    var elem = ast.getSysMLElement(0);
    if (!(ast.getSysMLElement(0) instanceof ASTAnonymousUsage)) {
      throw new IllegalStateException(
          "Expected first calc body element to be ASTAnonymousUsage, but got: "
              + ast.getSysMLElement(0).getClass().getName()
      );
    }

    ASTAnonymousUsage anon = (ASTAnonymousUsage) elem;

    if (anon.getSpecializationList().isEmpty()) {
      throw new IllegalStateException("Calc return has no specialization: " + adaptee.getFullName());
    }

    ASTSpecialization spec = anon.getSpecialization(0);

    if (spec.getSuperTypesList().isEmpty()) {
      throw new IllegalStateException("Calc return specialization has no super type: " + adaptee.getFullName());
    }

    var returnTypeAst = spec.getSuperTypesList().get(0);

    if (returnTypeAst.getDefiningSymbol().isPresent() && returnTypeAst.getDefiningSymbol().get() instanceof TypeSymbol) {
      TypeSymbol typeSymbol = (TypeSymbol) returnTypeAst.getDefiningSymbol().get();
      return SymTypeExpressionFactory.createFromSymbol(typeSymbol);
    }

    // fallback for primitive names if needed
    String printed = returnTypeAst.printType();
    return SymTypeExpressionFactory.createPrimitive(printed);
  }
}
