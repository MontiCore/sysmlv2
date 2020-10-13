package de.monticore.lang.sysml.cocos.imports;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.cocos.naming.NameReference;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ImportWithStarAndWithAsTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Ignore
  @Test
  public void testValid() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/simple");
    NameReference coco = new NameReference(); //TODO change this here
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }
    printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Ignore
  @Test
  public void testInvalid() {
    List<ASTUnit> models = this.invalidParseAndBuildSymbolsInSubDir("/imports/simple"); //TODO
    NameReference coco = new NameReference(); //TODO
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo(coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }

    assertEquals(1, Log.getFindings().size()); //TODO
    assertTrue(Log.getFindings().stream().findFirst().get().isError());
    this.printAllFindings(); //TODO
    Collection<Finding> expectedWarnings = Arrays.asList(Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.NameReference)) + //TODO
        " Reference NeverDefined could not be resolved.", new SourcePosition(2, 14, "ReferenceIsMissing.sysml")));

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}
