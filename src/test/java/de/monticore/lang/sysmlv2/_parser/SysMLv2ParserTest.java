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

public class SysMLv2ParserTest {

  private static final String INTRO_PATH = "src/test/resources/parser/";

  @Test
  public void testS5() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s5.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS6() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s6.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS7() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s7.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS8() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s8.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS9() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s9.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS10() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s10.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS11() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s11.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS12() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s12.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS13() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s13.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS14() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s14.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS15() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s15.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS16() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s16.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS17() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s17.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS23() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s23.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS25() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s25.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS26() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s26.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS27() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s27.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS28() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s28.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS30() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s30.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS31() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s31.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS32() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s32.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS34() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s34.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS35() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s35.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS36() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s36.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS37() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s37.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS38() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s38.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS39() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s39.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS40() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s40.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS41() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s41.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS42() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s42.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS43() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s43.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS44() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s44.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS45() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s45.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS46() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s46.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Ignore
  @Test
  public void testS47() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s47.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testS49() throws IOException {
    Path model = Paths.get(INTRO_PATH + "s49.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

}
