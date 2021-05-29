/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ResolveBetweenScopesTest extends AbstractSysMLTest {

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  @Ignore // TODO fix me
  public void resolveToOtherScopeDirectImportTest(){

    String modelPath = this.pathToValidModels + "/imports/simple";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    ISysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    Optional<SysMLTypeSymbol> packageWithImport = topScope.resolveSysMLType("Import Vehicle");
    assertTrue(packageWithImport.isPresent());
    Optional<SysMLTypeSymbol> vehicleSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Vehicle");
    Optional<SysMLTypeSymbol> busSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Bus");
    Optional<SysMLTypeSymbol> alreadyInScopeSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("AlreadyInScope");
    this.printAllFindings();
    assertTrue(vehicleSym.isPresent());
    assertFalse(busSym.isPresent());
    assertTrue(alreadyInScopeSym.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void resolveToOtherScopeStarImportTest(){

    String modelPath = this.pathToValidModels + "/imports/starImport";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    ISysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    Optional<SysMLTypeSymbol> packageWithImport = topScope.resolveSysMLType("Import Vehicle");
    assertTrue(packageWithImport.isPresent());
    Optional<SysMLTypeSymbol> vehicleSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Vehicle");
    Optional<SysMLTypeSymbol> busSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Bus");
    Optional<SysMLTypeSymbol> alreadyInScopeSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("AlreadyInScope");
    this.printAllFindings();
    assertTrue(vehicleSym.isPresent());
    assertTrue(busSym.isPresent());
    assertTrue(alreadyInScopeSym.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void resolveToOtherScopeAsImportTest(){

    String modelPath = this.pathToValidModels + "/imports/importAs";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    ISysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    Optional<SysMLTypeSymbol> packageWithImport = topScope.resolveSysMLType("Import Vehicle");
    assertTrue(packageWithImport.isPresent());
    Optional<SysMLTypeSymbol> vehicleSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Vehicle");
    Optional<SysMLTypeSymbol> busSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Bus");
    Optional<SysMLTypeSymbol> alreadyInScopeSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("AlreadyInScope");
    Optional<SysMLTypeSymbol> myVehicleNameInThisScopeSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("MyVehicleNameInThisScope");
    this.printAllFindings();
    assertFalse(vehicleSym.isPresent());
    assertFalse(busSym.isPresent());
    assertTrue(alreadyInScopeSym.isPresent());
    assertTrue(myVehicleNameInThisScopeSym.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
}
