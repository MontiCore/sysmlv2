package sysml4verification;

import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageTest {

  private static final String MODEL_PATH = "src/test/resources/sysml4verification/parser/";

  @BeforeEach
  public void clearFindings() {
    Log.clearFindings();
  }

  /**
   * Checks that the models do not dusturb the symbol table creation process
   */
  @Test
  public void testLanguage_getGlobalScopeFor() throws IOException {
    var scope = SysML4VerificationLanguage.getGlobalScopeFor(Paths.get(MODEL_PATH));
    assertTrue(Log.getFindings().isEmpty());
  }

}
