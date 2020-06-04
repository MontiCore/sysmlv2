

package de.monticore.lang.sysml.utils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * TODO implement me
 *
 * @author Robin Muenstermann
 * @version 1.0
 */
public class Parser {

  private String pathToFile;
  Parser(String pathToFile){
    this.pathToFile = pathToFile;
  }

  /*void parse(){
    SysML2Parser parser = new SysML2Parser();// TODO
    Path model = Paths.get(this.pathToFile);

    try {
      Optional<ASTSysMLPackage> sysmlPackage = parser.parse(model.toString());//TODO
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }*/
}
