package de.monticore.lang.sysmlvariant.parser;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.monticore.lang.sysmlvariant.sysmlextended._parser.SysMLExtendedParser;
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

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLExtendedTest extends AbstractSysMLTest {
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
    SysMLExtendedParser parser = new SysMLExtendedParser();
    Path model = Paths.get(this.pathToVariantModels + "/extended/example1.sysmle");
    try {
      Optional<ASTUnit> sysmlPackage = parser.parse(model.toString());
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
    SysMLExtendedParser parser = new SysMLExtendedParser();
    Path model = Paths.get(this.pathToVariantModels + "/extended/invalid/invalid1.sysmle");
    try {
      Optional<ASTUnit> sysmlPackage = parser.parse(model.toString());
    }
    catch (IOException e) {
    }
    //printAllFindings();
    assertTrue(parser.hasErrors());
  }


}
