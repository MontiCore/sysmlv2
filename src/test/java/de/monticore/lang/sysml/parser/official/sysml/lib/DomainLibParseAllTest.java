/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.parser.official.sysml.lib;

import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Test;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class DomainLibParseAllTest {
  private final String pathToDir= "src/test/resources/examples/officialPilotImplementation/2020/03/"
      + "library/Domain Libraries" ;

  public void parseSysML(String path) {
    SysMLParserForTesting parser = new SysMLParserForTesting();
    parser.parseSysML(path);
  }

  @Test
  public void parse_Geometry_BasicGeometryTest(){
    this.parseSysML( pathToDir +  "/Geometry/BasicGeometry.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_ISQTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/ISQ.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_QuantitiesTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/Quantities.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_SITest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/SI.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_SIPrefixesTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/SIPrefixes.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_TimeTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/Time.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_UnitsAndScalesTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/UnitsAndScales.sysml");
  }

  @Test
  public void parse_Quantities_and_Units_USCustomaryUnitsTest(){
    this.parseSysML( pathToDir +  "/Quantities and Units/USCustomaryUnits.sysml");
  }

}
