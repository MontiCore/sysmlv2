package de.monticore.lang.sysmlvariant.parser;

import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.monticore.lang.sysmlvariant.basics.objectoriented.sysmlimportsandpackagesoo._ast.ASTPackage;
import de.monticore.lang.sysmlvariant.sysmlforstructureoo._parser.SysMLForStructureOOParser;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLForStructureOOTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  public void parseSysMLVariantExample1Test() {
    Log.enableFailQuick(false);
    SysMLForStructureOOParser parser = new SysMLForStructureOOParser();
    Path model = Paths.get(this.pathToVariantModels + "/structureoo/example1.sysmloo");
    try {
      Optional<ASTPackage> sysmlPackage = parser.parse(model.toString());
      printAllFindings();
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }

  @Test
  public void parseSysMLVariantInvalid1Test() {
    Log.enableFailQuick(false);
    SysMLForStructureOOParser parser = new SysMLForStructureOOParser();
    Path model = Paths.get(this.pathToVariantModels + "/structureoo/invalid/invalid1.sysmloo");
    try {
      Optional<ASTPackage> sysmlPackage = parser.parse(model.toString());
    }
    catch (IOException e) {
    }
    // printAllFindings();
    assertTrue(parser.hasErrors());
  }
  @Test
  public void parseSysMLVariantInvalid2Test() {
    Log.enableFailQuick(false);
    SysMLForStructureOOParser parser = new SysMLForStructureOOParser();
    Path model = Paths.get(this.pathToVariantModels + "/structureoo/invalid/invalid2.sysmloo");
    try {
      Optional<ASTPackage> sysmlPackage = parser.parse(model.toString());
    }
    catch (IOException e) {
    }
    //printAllFindings();
    assertTrue(parser.hasErrors());
  }
  @Test
  public void parseSysMLVariantInvalid3Test() {
    Log.enableFailQuick(false);
    SysMLForStructureOOParser parser = new SysMLForStructureOOParser();
    Path model = Paths.get(this.pathToVariantModels + "/structureoo/invalid/invalid3.sysmloo");
    try {
      Optional<ASTPackage> sysmlPackage = parser.parse(model.toString());
    }
    catch (IOException e) {
    }
    //printAllFindings();
    assertTrue(parser.hasErrors());
  }
}
