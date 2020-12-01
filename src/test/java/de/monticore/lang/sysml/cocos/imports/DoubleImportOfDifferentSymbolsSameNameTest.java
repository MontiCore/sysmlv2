package de.monticore.lang.sysml.cocos.imports;

import de.monticore.cocos.helper.Assert;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTImportUnitStdCoCo;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class DoubleImportOfDifferentSymbolsSameNameTest extends AbstractSysMLTest {
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
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/starImport");
    ImportStatementValid coco = new ImportStatementValid();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) coco);
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }
    printAllFindings();
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() {
    List<ASTUnit> models = this.invalidParseAndBuildSymbolsInSubDir("/imports/TwoSymbolsWithSameName");
    ImportStatementValid coco = new ImportStatementValid();
    SysMLCoCoChecker coCoChecker = new SysMLCoCoChecker();
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) coco);
    coCoChecker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) coco);
    for (ASTUnit model : models) {
      coCoChecker.checkAll(model);
    }

    //this.printAllFindings();
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().findFirst().get().isWarning());

    Collection<Finding> expectedWarnings =
        Arrays.asList(Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.DoubleImportOfDifferentSymbolsSameName))
            + " Did not import symbol Car\", because a symbol with the same name also gets imported into the scope.", new SourcePosition(9, 4, "Import Vehicle.sysml")),
            Finding.warning(SysMLCoCos.getErrorCode((SysMLCoCoName.DoubleImportOfDifferentSymbolsSameName)) +
                " Did not import symbol Car\", because a symbol with the same name also gets imported into the scope.", new SourcePosition(10, 4, "Import Vehicle.sysml")));

    Assert.assertErrors(expectedWarnings, Log.getFindings());
  }
}