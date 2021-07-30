/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.cocos.naming;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ArtifactStartsWithPackageTest extends AbstractSysMLTest {

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
    ASTUnit astUnit =
        this.parseSysMLSingleModel(this.pathToOfficialSysMLTrainingExamples + "/02. Blocks/Blocks Example.sysml");
    ArtifactStartsWithPackage coco = new ArtifactStartsWithPackage();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() {
    ASTUnit astUnit = this.parseSysMLSingleModel(this.pathToInvalidModels
        + "/artifactStartsWithBlock/BlockIsFirstElement.sysml");
    ArtifactStartsWithPackage coco = new ArtifactStartsWithPackage();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    coCoChecker.checkAll(astUnit);

    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());
    Collection<Finding> expectedWarnings = Arrays.asList(
        Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.ArtifactStartsWithPackage))+
                " Each artifact should start with its a package to organize the model elements.",
            new SourcePosition(2, 0, "BlockIsFirstElement.sysml"))
    );

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
