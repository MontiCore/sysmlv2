package de.monticore.lang.sysml.parser.examples;

import de.monticore.lang.sysml.block._ast.ASTBlockUnit;
import de.monticore.lang.sysml.block._parser.BlockParser;
import de.monticore.lang.sysml.sysmlbasics._ast.ASTUnitPrefix;
import de.monticore.lang.sysml.sysmlbasics._parser.SysMLBasicsParser;
import de.monticore.lang.sysml.utils.GenericParser;
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
public class UnitPrefixTest {

  @Test
  public void unitPrefixTest(){
    SysMLBasicsParser parser = new SysMLBasicsParser();
    Path model = Paths.get("src/test/resources/testing/UnitPrefix.sysml");
    try {
      Optional<ASTUnitPrefix> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }catch( IOException e){
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }

  /*@Test TODO First fix the GenericParser
  public void UnitPrefixGenericTest(){
    SysMLBasicsParser parser = new SysMLBasicsParser();
    Path model = Paths.get("src/test/resources/testing/UnitPrefix.sysml");
    GenericParser<SysMLBasicsParser, ASTUnitPrefix> genericParser =
            new GenericParser<>(model, parser);
    genericParser.parse();
  }*/

}
