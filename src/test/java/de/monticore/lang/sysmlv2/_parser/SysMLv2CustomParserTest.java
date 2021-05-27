package de.monticore.lang.sysmlv2._parser;


import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SysMLv2CustomParserTest {

  private static final String INTRO_PATH = "src/test/resources/parser/custom/";

  @Test
  public void testACDefInACDef() throws IOException {
    Path model = Paths.get(INTRO_PATH + "acdef_in_def.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testOpaqueACDefInACDef() throws IOException {
    Path model = Paths.get(INTRO_PATH + "opaque_acdef_in_def.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }


}
