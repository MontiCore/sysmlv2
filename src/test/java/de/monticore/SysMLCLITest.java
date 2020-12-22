package de.monticore;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import javax.swing.text.html.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class SysMLCLITest {
	@Test
	public void SysMLCLIPrettyPrinterTest() {
		Log.enableFailQuick(false);
		try {
			String path = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src"
				+ "/training/01. Packages/Comment Example.sysml";
			SysMLParser parser = new SysMLParser();
			Optional<ASTUnit> unitexp = parser.parse(path);
			SYSMLCLI.main(new String[]{"-i " + path, "-pp", path + "test.sysml"});
			Optional<ASTUnit> unitact = parser.parse(path + "test.sysml");
			(new File(path + "test.sysml")).delete();
			assertTrue(unitexp.isPresent() && unitact.isPresent() && unitexp.get().deepEquals(unitact.get()));
			//assertTrue(true);
		} catch (IOException e) {
			assertTrue(false);
		}
	}
}
