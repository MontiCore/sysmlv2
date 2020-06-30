package de.monticore.lang.sysml.parser.officialImpl.sysml.src.training;

import de.monticore.lang.sysml.sysml._ast.ASTSysMLRoot;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ParsingTrainingTest {

  @Test
  public void parseAllSpecifiedTrainingFilesTest(){
    PathsToFile pathsToFile = new PathsToFile();
    List<String> fullRelPaths = pathsToFile.getFullRelativePathToTrainingFiles();
    for (String path: fullRelPaths) {
      SysMLParser parser = new SysMLParser();
      Path model = Paths.get(path);
      try {
        Log.enableFailQuick(false);
        Optional<ASTSysMLRoot> sysmlPackage = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(sysmlPackage.isPresent());
      }catch( IOException e){
        e.printStackTrace();
        fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
      }
    }

  }
}
