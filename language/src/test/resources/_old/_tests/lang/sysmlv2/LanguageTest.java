package de.monticore.lang.sysmlv2;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageTest {

  private static final String MODEL_PATH = "src/test/resources/sysmlv2/parser/custom";

  @BeforeAll
  public static void setup() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void clearFindings() {
    Log.clearFindings();
  }

  /**
   * Checks that the models do not dusturb the symbol table creation process
   */
  @Test
  public void testLanguage_getGlobalScopeFor() throws IOException {
    var scope = SysMLv2Language.getGlobalScopeFor(Paths.get(MODEL_PATH));
    assertTrue(Log.getFindings().isEmpty());
  }

}
