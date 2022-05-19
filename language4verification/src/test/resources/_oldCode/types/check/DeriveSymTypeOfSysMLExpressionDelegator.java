package schrott.types.check;

import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverser;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;

import java.util.Optional;

/**
 * For a more detailed explanation on MontiCore TypeChecks see:
 * https://github.com/MontiCore/monticore/blob/6.7.0/monticore-grammar/src/main/java/de/monticore/types/check/TypeCheck.md
 * <p>
 * This class acts as the TypesCalculator for the SysML4Verification language.
 * <p>
 * It delegates to all already implemented TypesCalculators of the extended grammars by creating a SysML4Vericiation
 * traverser and setting the respective TypesCalculator of the extended grammar, which is a Visitor (and possibly
 * handler) for the extended grammar.
 * <p>
 * Constructs type info for expressions (the "5+2" in "int a = 5+2").
 * Uses visitors for parts of the language. Coordinates "TypeCheckResult" between the visitors.
 */

public class DeriveSymTypeOfSysMLExpressionDelegator implements IDerive {

  // TODO implement and add SysML specific type derivation classes

  TypeCheckResult typeCheckResult = new TypeCheckResult();

  private SysML4VerificationTraverser traverser;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfSysMLExpression deriveSymTypeOfSysMLExpression;

  private DeriveSymTypeOfOCLExpressions deriveSymTypeOfOCLExpressions;

  public DeriveSymTypeOfSysMLExpressionDelegator() {
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if (typeCheckResult.isPresentCurrentResult()) {
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }

  @Override
  public SysML4VerificationTraverser getTraverser() {
    return traverser;
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSysMLExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfOCLExpressions.setTypeCheckResult(typeCheckResult);
  }

  @Override
  public void init() {
    this.traverser = SysML4VerificationMill.traverser();
    this.typeCheckResult = new TypeCheckResult();

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();

    // TODO comment copied from MAV but should also be relevant here
    // We cannot use the normal Deriver here because it cannot check for the type of a 'port' name
    // deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionIncludingPorts();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfSysMLExpression = new DeriveSymTypeOfSysMLExpression();
    deriveSymTypeOfOCLExpressions = new DeriveSymTypeOfOCLExpressions();

    setTypeCheckResult(typeCheckResult);

    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
    traverser.add4SysMLExpressions(deriveSymTypeOfSysMLExpression);
    traverser.setSysMLExpressionsHandler(deriveSymTypeOfSysMLExpression);
    traverser.setOCLExpressionsHandler(deriveSymTypeOfOCLExpressions);
  }

}
