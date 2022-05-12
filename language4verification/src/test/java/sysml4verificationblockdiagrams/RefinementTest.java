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
 * Testklasse für die Angabe von Refinement-Relationen zwischen PartDefs
 */
public class RefinementTest {

  private final String RES = "src/test/resources/sysml4verificationblockdiagrams/refinement";

  @BeforeAll
  public static void init() {
    Log.init();
    SysML4VerificationMill.init();
  }

  /** Testet die Existenz des neuen Syntax-Elements */
  @Test
  public void testParser() throws IOException {
    Optional<ASTSysMLModel> model = SysML4VerificationMill.parser().parse_StringSysMLModel("part def A refines B, C;");
    assertThat(model).isPresent();
  }

  /** Testet die Getter-Hilfsmethode */
  @Test
  public void testRetrieval() throws IOException {
   ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES),true);

    PartDefSymbol A = globalScope.resolvePartDef("valid.A").get();
    assertThat(((ASTPartDef)A.getAstNode()).getRefinements()).isEmpty();

    PartDefSymbol B = globalScope.resolvePartDef("valid.B").get();
    var refinements = ((ASTPartDef)B.getAstNode()).getRefinements();
    assertThat(refinements).hasSize(1);
    assertThat(refinements.get(0).getName()).isEqualTo("A");

    PartDefSymbol C = globalScope.resolvePartDef("valid.C").get();
    refinements = ((ASTPartDef)C.getAstNode()).getRefinements();
    assertThat(refinements).hasSize(2);
    assertThat(refinements.get(0).getName()).isEqualTo("A");
    assertThat(refinements.get(1).getName()).isEqualTo("B");
  }

}
