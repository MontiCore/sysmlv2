package de.monticore.lang.sysmlv2._parser;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parser with custom examples based on the slide set ({@link SysMLv2SlidesParserTest})
 */
public class SysMLv2CustomParserTest {

  private static final String MODEL_PATH = "src/test/resources/sysmlv2/parser/custom/";

  @Test
  public void testEmptyPackage() throws IOException {
    Path model = Paths.get(MODEL_PATH + "EmptyPackage.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    assertEquals(1, ast.get().sizeSysMLElements());
    assertTrue(ast.get().getSysMLElement(0) instanceof ASTSysMLPackage);
    System.out.println(ast.get());
  }

  @ParameterizedTest(name = "{index} - {0} does parse w/o errors")
  @ValueSource(strings = {
      "Aut_Initial_Output.sysml",
      "Exhibit_States.sysml",
      "qualified_send.sysml",
      "opaque_acdef_in_element.sysml",
      "acdef_in_element.sysml",
      "opaque_acdef_in_def.sysml",
      "acdef_in_def.sysml",
      "transition_noname.sysml",
      "transition_sendaction.sysml",
      "DLUF.sysml",
      "DLUFv2.sysml",
      "DLUFv3.sysml",
      "DLUFv4.sysml",
      "DLUFv5.sysml",
      "DLUFv6.sysml"
  })
  public void testParsingModels(String path) throws IOException {
    Path model = Paths.get(MODEL_PATH + path);

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
