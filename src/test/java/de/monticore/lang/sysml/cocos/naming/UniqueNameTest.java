package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class UniqueNameTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  public void testValid() {
    String modelPath =this.pathToValidModels + "/UniqueName/UniqueName.sysml";
    ASTUnit astUnit =
        this.parseSysMLSingleModel(modelPath);

    SysMLTool.buildSymbolTablePathToSingleFile(modelPath, astUnit);
    UniqueName coco = new UniqueName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidDoesNotStartWithCapitalLetter() {
    String modelPath = this.pathToInvalidModels + "/UniqueName/TwoNamesInScope.sysml";
    ASTUnit astUnit =
        this.parseSysMLSingleModel(modelPath);

    SysMLTool.buildSymbolTablePathToSingleFile(modelPath, astUnit);
    UniqueName coco = new UniqueName();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    // printAllFindings();

    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());

    // System.out.println(getFindingsOutput());

    assertTrue(getFindingsOutput().contains(SysMLCoCos.getErrorCode((SysMLCoCoName.UniqueName)) +" Name \"Car\" "
            + "is not unique in its scope. Check definitions in scope at:"));
    /*
    Cannot test the following way, because the order is random (3,2 or 2,2 first)
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.error(SysMLCoCos.getErrorCode((SysMLCoCoName.UniqueName)) +
            " 0xSysML05 Name Car is not unique in its scope. Check definitions in scope at:"
            + " TwoNamesInScope.sysml:<3,2>,TwoNamesInScope.sysml:<2,2>")
    );
    Assert.assertErrors(expectedWarnings, Log.getFindings());*/
  }
}
