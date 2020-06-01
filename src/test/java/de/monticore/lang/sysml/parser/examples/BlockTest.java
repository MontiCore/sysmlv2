package de.monticore.lang.sysml.parser.examples;


import de.monticore.lang.sysml.block._ast.ASTBlockUnit;
import de.monticore.lang.sysml.block._parser.BlockParser;
import org.junit.Test;

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
public class BlockTest {


  @Test
  public void basicBlockTest(){

    BlockParser parser = new BlockParser(); //TODO wrong parsing should not block all tests
    /*Running de.monticore.lang.sysml.parser.examples.BlockTest
    14:47:48.079 [main] ERROR ROOT - blockExample.sysml:<1,13>: extraneous input '{' expecting Name
    */
    /*Path model = Paths.get("src/test/resources/testing/blockExample.sysml");
    try {
      Optional<ASTBlockUnit> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }catch( IOException e){
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }*/
  }

}
