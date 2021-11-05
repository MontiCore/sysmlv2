package de.monticore.lang.sysmlv2._parser;


import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparametrics._symboltable.ConstraintDefSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2ScopesGenitorDelegator;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class SysMLv2CustomParserTest {

  private static final String MODEL_PATH = "src/test/resources/parser/custom/";

  @Test
  public void testACDefInACDef() throws IOException {
    Path model = Paths.get(MODEL_PATH + "acdef_in_def.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testOpaqueACDefInACDef() throws IOException {
    Path model = Paths.get(MODEL_PATH + "opaque_acdef_in_def.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testACDefInACElement() throws IOException {
    Path model = Paths.get(MODEL_PATH + "acdef_in_element.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testOpaqueACDefInACElement() throws IOException {
    Path model = Paths.get(MODEL_PATH + "opaque_acdef_in_element.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testQualifiedSend() throws IOException {
    Path model = Paths.get(MODEL_PATH + "qualified_send.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
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

  @Test
  public void testInitialOutput() throws IOException {
    Path model = Paths.get(MODEL_PATH + "Aut_Initial_Output.sysml");

    SysMLv2Mill.init();
    SysMLv2Parser parser = SysMLv2Mill.parser();
    Optional<ASTSysMLModel> ast = parser.parse(model.toString());

    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }
}
