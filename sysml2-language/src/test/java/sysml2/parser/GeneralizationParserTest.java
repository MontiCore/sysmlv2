/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package sysml2.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.monticore.lang.sysml.legacy.sysml2._ast.ASTSysMLPackage;
import de.monticore.lang.sysml.legacy.sysml2._parser.SysML2Parser;

class GeneralizationParserTest {
  
  @Test
  void testGeneralization() {
    SysML2Parser parser = new SysML2Parser();
    Path model = Paths.get("src/test/resources/sysml2/parser/03.Generalization/Generalization Example.sysml");
    
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
