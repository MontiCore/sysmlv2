package sysml4verificationblockdiagrams;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationGlobalScope;
import de.monticore.lang.sysml4verificationblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLBasisNode;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TrustLevelTest {

  private final String RES = "src/test/resources/sysml4verificationblockdiagrams/security";

  @BeforeAll
  public static void init() {
    Log.init();
    SysML4VerificationMill.init();
  }

  /** Testet die Existenz des neuen Syntax-Elements */
  @Test
  public void testParser() throws IOException {
    Optional<ASTSysMLModel> model = SysML4VerificationMill.parser().parse_StringSysMLModel("part def meinBlock { trustLevel -1 \"physical lock\"; }");
    assertThat(model).isPresent();
  }

  /** Testet die Getter-Hilfsmethode */
  @Test
  public void testRetrieval() throws IOException {
    ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES),true);

    PartDefSymbol secure = globalScope.resolvePartDef("valid.SecurePart").get();
    assertThat(((ASTPartDef)secure.getAstNode()).getRelativeTrustLevel()).isEqualTo(1);

    PartDefSymbol insecure = globalScope.resolvePartDef("valid.SecurityRiskyPart").get();
    assertThat(((ASTPartDef)insecure.getAstNode()).getRelativeTrustLevel()).isEqualTo(-10);
  }

  /** Testet die UniqueTrustLevelStatement CoCo */
  @Test
  public void testUniqueTrustLevelCoCo() throws IOException {
    Log.enableFailQuick(false);
    ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES),true);

    PartDefSymbol invalid = globalScope.resolvePartDef("invalidTL.InvalidPart").get();
    SysML4VerificationCoCoChecker.beforeSymbolTableCreation().checkAll(
        (ASTSysMLBasisNode) invalid.getAstNode());

    /*The UniqueTrustLevelStatement CoCo check should yield an error because there are two trustLevel statements
      in one part*/
    assertThat(Log.getErrorCount()).isEqualTo(1);
    assertThat(Log.getFindings().get(0).getMsg()).isEqualTo("There is more than one trust level statement in part def "+
        "InvalidPart"+" but only one per part def is allowed.");
  }

}
