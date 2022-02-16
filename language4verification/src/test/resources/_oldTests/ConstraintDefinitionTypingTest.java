package schrotttests;

import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTAssertConstraintUsageCoCo;
import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification._ast.ASTSysML4VerificationNode;
import de.monticore.lang.sysml4verification._cocos.ConstraintDefinitionIsPresentCoCo;
import de.monticore.lang.sysml4verification._cocos.ConstraintExpressionTypeIsBooleanCoCo;
import de.monticore.lang.sysml4verification._cocos.ConstraintParameterTypeIsValidCoCo;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.BlockSymbol;
import de.monticore.lang.sysml4verification._symboltable.ConstraintDefinitionSymbol;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet die Implementationen {@link ConstraintExpressionTypeIsBooleanCoCo} und {@link ConstraintParameterTypeIsValidCoCo}
 * und {@link ConstraintDefinitionIsPresentCoCo}
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConstraintDefinitionTypingTest {

  private static final String BASE = "src/test/resources/cocos/types";

  private static ISysML4VerificationScope scope;

  @BeforeAll
  public void scope() throws IOException {
    scope = SysML4VerificationLanguage.getGlobalScopeFor(Path.of(BASE), true);
  }

  private static Stream<Arguments> badParameters() {
    return Stream.of(
        Arguments.of("invalid.NoMaths", "Couldn't derive type of expression"),
        Arguments.of("invalid.StrMaths", "Wrong type"),
        Arguments.of("invalid.ParaMaths", "Wrong type"),
        // Error code stems from the OCL implementation and indicates that the type of the expression in a OCL construct
        // couldn't be calculated
        Arguments.of("invalid.OCLMaths", "0xA3211"));
  }

  @BeforeEach
  public void init() {
    LogStub.init();
  }

  /** Testet valide Assertions */
  @ParameterizedTest
  @ValueSource(strings = {"valid.QuickMaths", "valid.SlowMaths", "valid.ParaMaths", "valid.OCLMaths"})
  public void testValid(String constraintDef) {
    Optional<ConstraintDefinitionSymbol> constraintDefinitionSymbol = scope.resolveConstraintDefinition(constraintDef);
    assertThat(constraintDefinitionSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new ConstraintExpressionTypeIsBooleanCoCo());
    checker.checkAll((ASTSysML4VerificationNode) constraintDefinitionSymbol.get().getAstNode());

    assertThat(Log.getFindings()).isEmpty();
    Log.clearFindings();

    checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new ConstraintParameterTypeIsValidCoCo());
    checker.checkAll((ASTSysML4VerificationNode) constraintDefinitionSymbol.get().getAstNode());

    assertThat(Log.getFindings()).isEmpty();
  }

  /** Testet invalide Assertions */
  @ParameterizedTest
  @MethodSource("badParameters")
  public void testInvalid(String constraintDef, String errorCode) {
    Optional<ConstraintDefinitionSymbol> constraintDefinitionSymbol = scope.resolveConstraintDefinition(constraintDef);
    assertThat(constraintDefinitionSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new ConstraintExpressionTypeIsBooleanCoCo());
    checker.checkAll((ASTSysML4VerificationNode) constraintDefinitionSymbol.get().getAstNode());

    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().startsWith(errorCode));
  }

  /** Testet invalide Type Assertions */
  @ParameterizedTest
  @ValueSource(strings = {"invalid.BadType"})
  public void testInvalidType(String fqn) {
    Optional<ConstraintDefinitionSymbol> constraintDefinitionSymbol = scope.resolveConstraintDefinition(fqn);
    assertThat(constraintDefinitionSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new ConstraintParameterTypeIsValidCoCo());
    checker.checkAll((ASTSysML4VerificationNode) constraintDefinitionSymbol.get().getAstNode());

    // Error code is used in MC to indicate that a type can't be found
    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().startsWith("0xA0324"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid.BadPartDefNonExistent"})
  public void testInvalidDefinition(String partDef) {
    Optional<BlockSymbol> blockSymbol = scope.resolveBlock(partDef);
    assertThat(blockSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo((SysMLConstraintsASTAssertConstraintUsageCoCo) new ConstraintDefinitionIsPresentCoCo());
    checker.checkAll((ASTSysML4VerificationNode) blockSymbol.get().getAstNode());

    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().startsWith("Can't resolve constraint"));
  }
}
