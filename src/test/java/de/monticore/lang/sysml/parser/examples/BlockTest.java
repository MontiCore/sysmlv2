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
    BlockParser parser = new BlockParser();
    Path model = Paths.get("src/test/resources/testing/blockExample.sysml");
    try {
      Optional<ASTBlockUnit> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }catch( IOException e){
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }

}
