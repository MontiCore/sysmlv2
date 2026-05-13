package parser;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonUniqueWithoutMultiplicityParserTest {
  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    SysMLv2Mill.init();
  }

  /**
   *  Modifier "nonunique" can be applied to elements without
   *  explicit star-multiplicity. This is because the cardinality
   *  might be inherited from the super type.
   */
  @Test
  void testNonUniqueWithoutMultiplicity() throws IOException {
    Log.clearFindings();
    SysMLv2Parser parser = SysMLv2Mill.parser();

    var model = "attribute a: A nonunique :> SuperType;";

    Optional<ASTSysMLModel> ast = parser.parse_String(model);

    assertFalse(parser.hasErrors(), "Parsing should not have failed");
    assertTrue(ast.isPresent(), "The AST should have been created");
    assertTrue(Log.getFindings().isEmpty(), "No parser findings expected");
  }
}
