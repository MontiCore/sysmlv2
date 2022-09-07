package schrotttests;

import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTAssertConstraintUsageCoCo;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification._ast.ASTSysML4VerificationNode;
import de.monticore.lang.sysml4verification._cocos.ConstraintParameterTypeIsValidCoCo;
import de.monticore.lang.sysml4verification._cocos.ParameterUsageIsValidCoCo;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.BlockSymbol;
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
 * Testet die Implementationen {@link ParameterUsageIsValidCoCo} und {@link ConstraintParameterTypeIsValidCoCo}
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParameterTypeTest {

  private static final String BASE = "src/test/resources/cocos/types";

  private static ISysML4VerificationScope scope;

  @BeforeAll
  public void scope() throws IOException {
    scope = SysML4VerificationLanguage.getGlobalScopeFor(Path.of(BASE), true);
  }

  private static Stream<Arguments> parameters() {
    return Stream.of(
        Arguments.of("invalid.PartTooManyArguments", "Too many arguments"),
        Arguments.of("invalid.PartTooFewArguments", "Missing argument for"),
        Arguments.of("invalid.PartUnderivableType", "Couldn't derive type of expression"),
        Arguments.of("invalid.PartWrongType", "Parameter has wrong type"));
  }

  @BeforeEach
  public void init() {
    LogStub.init();
  }

  /** Testet valide Assertions */
  @ParameterizedTest
  @ValueSource(strings = {"valid.GoodPart"})
  public void testValid(String partDef) {
    Optional<BlockSymbol> blockSymbol = scope.resolveBlock(partDef);
    assertThat(blockSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo((SysMLConstraintsASTAssertConstraintUsageCoCo) new ParameterUsageIsValidCoCo());
    checker.addCoCo((SysMLConstraintsASTConstraintUsageCoCo) new ParameterUsageIsValidCoCo());
    checker.checkAll((ASTSysML4VerificationNode) blockSymbol.get().getAstNode());

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("parameters")
  public void testTooMany(String partDef, String errorMessage) {
    Optional<BlockSymbol> blockSymbol = scope.resolveBlock(partDef);
    assertThat(blockSymbol).isPresent();

    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo((SysMLConstraintsASTAssertConstraintUsageCoCo) new ParameterUsageIsValidCoCo());
    checker.checkAll((ASTSysML4VerificationNode) blockSymbol.get().getAstNode());

    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().startsWith(errorMessage));
    Log.clearFindings();

    checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo((SysMLConstraintsASTConstraintUsageCoCo) new ParameterUsageIsValidCoCo());
    checker.checkAll((ASTSysML4VerificationNode) blockSymbol.get().getAstNode());

    assertThat(Log.getFindings()).anyMatch(f -> f.getMsg().startsWith(errorMessage));
  }
}
