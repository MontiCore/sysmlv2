/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.legacy.sysml2._parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.monticore.lang.sysml.legacy.sysml2._ast.ASTSysMLPackage;
import de.monticore.lang.sysml.legacy.sysml2._parser.SysML2Parser;

public class PartsParserTest {
  
  @Test
  public void testParts1() {
    SysML2Parser parser = new SysML2Parser();
    Path model = Paths.get("src/test/resources/sysml2/parser/06.Parts/Parts Example-1.sysml");
    
    try {
      Optional<ASTSysMLPackage> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }
  
  @Test
  public void testParts2() {
    SysML2Parser parser = new SysML2Parser();
    Path model = Paths.get("src/test/resources/sysml2/parser/06.Parts/Parts Example-2.sysml");
    
    try {
      Optional<ASTSysMLPackage> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }
  
}
