package de.monticore.lang.sysml.utils;

import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTUnit;
import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AbstractSysMLTest {
  public final String pathToInvalidModels = "src/test/resources/cocos/invalid";
  public final String pathToOfficialSysMLExamples = "src/test/resources/examples" + "/officialPilotImplementation/2020/03/sysml/src/training";

  public void setUpLog(){
    LogStub.init();
    Log.getFindings().clear();
  }

  public ASTUnit parseSysMLSingleModel(String path) {
    SysMLParserForTesting sysMLParserForTesting = new SysMLParserForTesting();
    Optional<ASTUnit> astUnit = sysMLParserForTesting.parseSysML(path);
    assertTrue(astUnit.isPresent());
    return astUnit.get();
  }

  public boolean printAllFindings() {
    List<Finding> findingsList = Log.getFindings();
    System.out.println("I found " + findingsList.size() + " findings:");
    for (Finding f : findingsList) {
      System.out.println("    -> Finding: " + f.toString());
    }
    return false;
  }
}
