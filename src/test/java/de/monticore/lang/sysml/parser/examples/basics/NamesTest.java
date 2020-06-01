package de.monticore.lang.sysml.parser.examples.basics;

import de.monticore.lang.sysml.basics.names._ast.ASTNamesNode;
import de.monticore.lang.sysml.basics.names._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.names._parser.NamesParser;
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
public class NamesTest {


  private void checkParser(Path model){
    try {
      NamesParser parser = new NamesParser();
      Optional<ASTQualifiedName> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }catch( IOException e){
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }
  @Test
  public void colonNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/colonName.sysml");
    checkParser(model);
  }

  @Test
  public void dotNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/dotName.sysml");
    checkParser(model);
  }
  @Test
  public void simpleNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/simpleName.sysml");
    checkParser(model);
  }
}
