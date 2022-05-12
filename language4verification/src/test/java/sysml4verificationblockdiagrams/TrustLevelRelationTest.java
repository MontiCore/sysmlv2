package sysml4verificationblockdiagrams;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationGlobalScope;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLBasisNode;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TrustLevelRelationTest {

  private final String RES = "src/test/resources/sysml4verificationblockdiagrams/security";

  @BeforeAll
  public static void init() {
    Log.init();
    SysML4VerificationMill.init();
  }

  /** Testet die Existenz des neuen TrustLevelRelation Syntax-Elements */
  @Test
  public void testParser() throws IOException {
    Optional<ASTSysMLModel> modelSmaller = SysML4VerificationMill.parser().parse_StringSysMLModel(
        "part def meinBlock { trustLevelRelation einPart < zweiPart; }");
    assertThat(modelSmaller).isPresent();
    Optional<ASTSysMLModel> modelGreater = SysML4VerificationMill.parser().parse_StringSysMLModel(
        "part def meinBlock { trustLevelRelation einPart > zweiPart; }");
    assertThat(modelGreater).isPresent();
    Optional<ASTSysMLModel> modelEquals = SysML4VerificationMill.parser().parse_StringSysMLModel(
        "part def meinBlock { trustLevelRelation einPart == zweiPart; }");
    assertThat(modelEquals).isPresent();
  }

  /** Testet die CoCo fuer TrustLevelRelations */
  @Test
  public void testTrustLevelRelationCoCo() throws IOException {
    Log.enableFailQuick(false);
    ISysML4VerificationGlobalScope globalScope =
        SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(RES), Paths.get(RES), true);

    PartDefSymbol valid = globalScope.resolvePartDef("easyVerification.SecurePart").get();
    SysML4VerificationCoCoChecker.beforeSymbolTableCreation().checkAll(
        (ASTSysMLBasisNode) valid.getAstNode());

    assertThat(Log.getErrorCount()).isEqualTo(0);

    PartDefSymbol invalid = globalScope.resolvePartDef("invalidTLR.InvalidPart").get();
    SysML4VerificationCoCoChecker.beforeSymbolTableCreation().checkAll(
        (ASTSysMLBasisNode) invalid.getAstNode());

    // Two errors as the model contains two invalid TLR
    Set<String> errors = Log.getFindings().stream().map(f -> f.getMsg()).collect(Collectors.toSet());

    assertThat(Log.getErrorCount()).isEqualTo(2);
    assertThat(errors.contains("TL-relations must reference existing part usages. Could not"
        + " find "+"nonExistingPartProp"+" in the current context.")).isTrue();
    assertThat(errors.contains("Found symbol with name "+"portA"+" that is "
        + "not a part usage. Trust level relations can only be defined about part usages.")).isTrue();
  }
}
