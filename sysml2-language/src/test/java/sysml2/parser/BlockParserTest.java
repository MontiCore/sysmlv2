package sysml2.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sysml2._ast.ASTSysMLPackage;
import sysml2._parser.SysML2Parser;

class BlockParserTest {

	@Test
	void testBlocks() {
		SysML2Parser parser = new SysML2Parser();
		Path model = Paths.get("src/test/resources/sysml2/parser/02.Blocks/Blocks Example.sysml");
		
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
