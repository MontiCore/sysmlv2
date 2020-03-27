/* (c) https://github.com/MontiCore/monticore */
package sysml2.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.monticore.lang.sysml.legacy.sysml2._ast.ASTSysMLPackage;
import de.monticore.lang.sysml.legacy.sysml2._parser.SysML2Parser;

class PartsParserTest {

	@Test
	void testParts1() {
		SysML2Parser parser = new SysML2Parser();
		Path model = Paths.get("src/test/resources/sysml2/parser/06.Parts/Parts Example-1.sysml");
		
		try {
      Optional<ASTSysMLPackage> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": "
          + e.getMessage());
    }
	}
	
	 @Test
	  void testParts2() {
	    SysML2Parser parser = new SysML2Parser();
	    Path model = Paths.get("src/test/resources/sysml2/parser/06.Parts/Parts Example-2.sysml");
	    
	    try {
	      Optional<ASTSysMLPackage> sysmlPackage = parser.parse(model.toString());
	      assertFalse(parser.hasErrors());
	      assertTrue(sysmlPackage.isPresent());
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	      fail("There was an exception when parsing the model " + model + ": "
	          + e.getMessage());
	    }
	  }

}
