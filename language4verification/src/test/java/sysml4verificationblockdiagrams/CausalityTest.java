package sysml4verificationblockdiagrams;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationGlobalScope;
import de.monticore.lang.sysml4verificationblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testklasse für das Setzen von Timing in BlockDefinitions
 */
public class CausalityTest {

  private final String RES = "src/test/resources/sysml4verificationblockdiagrams/causality";

  @BeforeAll
  public static void init() {
    Log.init();
    SysML4VerificationMill.init();
  }

  /** Testet die Existenz des neuen Syntax-Elements */
  @Test
  public void testParser() throws IOException {
    Optional<ASTSysMLModel> model = SysML4VerificationMill.parser().parse_StringSysMLModel("part def meinBlock { timing delayed; }");
    assertThat(model).isPresent();
  }

  /** Testet die Getter-Hilfsmethode */
  @Test
  public void testRetrieval() throws IOException {
   ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES), true);

    PartDefSymbol delayed = globalScope.resolvePartDef("valid.DelayedPart").get();
    assertThat(((ASTPartDef)delayed.getAstNode()).isDelayed());

    PartDefSymbol instant = globalScope.resolvePartDef("valid.InstantPart").get();
    assertThat(((ASTPartDef)instant.getAstNode()).isInstant());
  }

  /** Testet den Default ("INSTANT") */
  public void testDefault() throws IOException {
    ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES), true);

    PartDefSymbol noTiming = globalScope.resolvePartDef("valid.NoTiming").get();
    assertThat(((ASTPartDef) noTiming.getAstNode()).isInstant());

    PartDefSymbol noTimingAndNoBoy = globalScope.resolvePartDef("valid.NoTimingAndNoBody").get();
    assertThat(((ASTPartDef) noTimingAndNoBoy.getAstNode()).isInstant());
  }

}
