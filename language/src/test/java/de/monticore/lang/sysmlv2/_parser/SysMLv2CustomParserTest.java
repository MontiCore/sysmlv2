package de.monticore.lang.sysmlv2._parser;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parser with custom examples based on the slide set ({@link SysMLv2SlidesParserTest})
 */
public class SysMLv2CustomParserTest {

  private static final String MODEL_PATH = "src/test/resources/sysmlv2/parser/custom/";

  private static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of("Aut_Initial_Output.sysml"),
        Arguments.of("Exhibit_States.sysml"),
        Arguments.of("qualified_send.sysml"),
        Arguments.of("opaque_acdef_in_element.sysml"),
        Arguments.of("acdef_in_element.sysml"),
        Arguments.of("opaque_acdef_in_def.sysml"),
        Arguments.of("acdef_in_def.sysml"),
        Arguments.of("transition_noname.sysml"),
        Arguments.of("transition_sendaction.sysml")
    );
  }

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

  @ParameterizedTest
  @MethodSource("createInputs")
  public void testParsingModels(String path) throws IOException {
    Path model = Paths.get(MODEL_PATH + path);

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }
}
