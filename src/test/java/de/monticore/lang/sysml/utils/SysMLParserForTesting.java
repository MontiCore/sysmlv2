package de.monticore.lang.sysml.utils;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLParserForTesting {
  public Optional<ASTUnit> parseSysML(String path) {
    Log.enableFailQuick(false);
    SysMLParser parser = new SysMLParser();
    Path model = Paths.get(path);
    try {
      Optional<ASTUnit> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
      return sysmlPackage;
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
      return Optional.empty();
    }
  }

  public Optional<ASTUnit> parseSysMLFromString(String model) {
    Log.enableFailQuick(false);
    SysMLParser parser = new SysMLParser();
    try {
      Optional<ASTUnit> sysmlPackage = parser.parse_String(model);
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
      return sysmlPackage;
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
      return Optional.empty();
    }
  }
}
