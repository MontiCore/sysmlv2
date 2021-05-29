/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.parser.official.sysml.lib;

import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Test;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SystemsLibParseAllTest {

  private final String pathToDir= "src/test/resources/examples/officialPilotImplementation/2020/03/"
      + "library/Systems Library" ;

  public void parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    parser.parseSysML(path);
  }
  @Test
  public void parse_ActivitiesTest(){
    this.parseSysML( pathToDir +  "/Activities.sysml");
  }

  @Test
  public void parse_BlocksTest(){
    this.parseSysML( pathToDir +  "/Blocks.sysml");
  }

  @Test
  public void parse_ConstraintsTest(){
    this.parseSysML( pathToDir +  "/Constraints.sysml");
  }

  @Test
  public void parse_ControlNodesTest(){
    this.parseSysML( pathToDir +  "/ControlNodes.sysml");
  }

  @Test
  public void parse_RequirementsTest(){
    this.parseSysML( pathToDir +  "/Requirements.sysml");
  }

  @Test
  public void parse_StatesTest(){
    this.parseSysML( pathToDir +  "/States.sysml");
  }

}
