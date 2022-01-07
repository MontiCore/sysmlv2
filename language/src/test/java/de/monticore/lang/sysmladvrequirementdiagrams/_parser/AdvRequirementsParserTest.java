package de.monticore.lang.sysmladvrequirementdiagrams._parser;

import de.monticore.lang.sysmlv2alt.SysMLv2AltMill;
import de.monticore.lang.sysmlv2alt._ast.ASTSysMLModel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Checks successful parsing of adv requirements.
 */
public class AdvRequirementsParserTest {

  @BeforeClass
  public static void initScope() {
    SysMLv2AltMill.init();
  }

  /**
   * Tests the model with AdvRequirementDefinition/Usage/Satisfaction clause was
   * parsed successfully.
   *
   * @throws IOException
   */
  @Test
  public void testAdvRequirements() throws IOException {
    String model = "src/test/resources/sysmladvrequirementdiagrams/_parser/AdvRequirements.sysml";
    ASTSysMLModel ast = SysMLv2AltMill.parser().parse(model).get();
    SysMLv2AltMill.scopesGenitorDelegator().createFromAST(ast);
  }
}
