package sysml4verification;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._cocos.FinalDirection;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLBasisNode;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportsAndPackagesNode;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class CoCoTest {

  @BeforeAll
  public static void log() {
    LogStub.init();
  }

  @Nested
  public class TestFinalDirection {

    private final String MODEL = "src/test/resources/sysml4verification/cocos/finaldirection/Model.sysml";

    private ISysML4VerificationScope globalScope;

    @BeforeEach
    public void clear() throws IOException {
      this.globalScope = SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(MODEL), true);
      Log.enableFailQuick(false);
      Log.clearFindings();
    }

    @Test
    public void checkValid() {
      var ast = (ASTSysMLImportsAndPackagesNode) globalScope.resolveSysMLPackage("Valid").get().getAstNode();

      var checker = new SysML4VerificationCoCoChecker();
      checker.addCoCo(new FinalDirection());
      checker.checkAll(ast);

      assertThat(Log.getFindings()).isEmpty();
    }

    @Test
    public void checkInvalid() {
      var ast = (ASTSysMLImportsAndPackagesNode) globalScope.resolveSysMLPackage("Invalid").get().getAstNode();

      var checker = new SysML4VerificationCoCoChecker();
      checker.addCoCo(new FinalDirection());
      checker.checkAll(ast);

      assertThat(Log.getFindings()).anyMatch(f -> f.isError() && f.getMsg().equals(FinalDirection.errorMessage));
    }

  }

}
