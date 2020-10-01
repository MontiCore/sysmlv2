package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._symboltable.ISharedBasisScope;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._symboltable.IImportsAndPackagesScope;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._symboltable.ImportsAndPackagesScope;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._symboltable.PackageSymbol;
import de.monticore.lang.sysml.basics.valuetypes._ast.ASTValueTypeStd;
import de.monticore.lang.sysml.basics.valuetypes._symboltable.ValueTypeStdSymbol;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.bdd._symboltable.BlockSymbol;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SymbolTableCreationTest extends AbstractSysMLTest {
  @Test
  public void testSuccessfulCreationInOneFile() {
    String currentPath = this.pathToOfficialSysMLTrainingExamples + "/02. Blocks/Blocks Example.sysml";

    //Ensure successful parsing
    ASTUnit astUnit =  this.parseSysMLSingleModel(currentPath);
    ASTPackageUnit packageUnit  = (ASTPackageUnit) astUnit;
    System.out.println("Package name is " + packageUnit.getPackage().getName());
    assertEquals("Blocks Example", packageUnit.getPackage().getName());
    ASTBlock block = (ASTBlock) packageUnit.getPackage().getPackageBody().getPackageMember(0).getPackagedDefinitionMember();
    System.out.println("Block name is " + block.getName());
    assertEquals("Blocks Example", packageUnit.getPackage().getName());

    //Creating Symboltable
    ModelPath mp = SysMLTool.createModelpath(currentPath);
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    SysMLArtifactScope topScope = helperSysMLSymbolTableCreator.createSymboltableSingleASTUnit(astUnit, mp);

    //Testing Symboltable
    Optional<PackageSymbol> packageSymbol = topScope.resolvePackage("Blocks Example");
    Optional<BlockSymbol> blockSymbol = topScope.getSubScopes().get(0).resolveBlockDown("Vehicle");
    Optional<ValueTypeStdSymbol> valueTypeSymbol = topScope.getSubScopes().get(0).resolveValueTypeStdDown(
        "VehicleStatus");
    Optional<PackageSymbol> notExistingSymbol = topScope.resolvePackage("WrongName Example");

    assertTrue(packageSymbol.isPresent());
    assertEquals("Blocks Example", packageSymbol.get().getName());
    assertTrue(blockSymbol.isPresent());
    assertEquals("Vehicle", blockSymbol.get().getName());
    assertTrue(valueTypeSymbol.isPresent());
    assertEquals("VehicleStatus", valueTypeSymbol.get().getName());
    assertFalse(notExistingSymbol.isPresent());


    SysMLArtifactScope scope = (SysMLArtifactScope) astUnit.getEnclosingScope();
    //Testing resolving with astUnit
    Optional<PackageSymbol> packageSymbolEnclosingScope = scope.resolvePackage("Blocks Example");
    Optional<BlockSymbol> blockSymbolEnclosingScope = scope.getSubScopes().get(0).resolveBlockDown("Vehicle");
    Optional<ValueTypeStdSymbol> valueTypeSymbolEnclosingScope = scope.getSubScopes().get(0).resolveValueTypeStdDown(
        "VehicleStatus");
    Optional<PackageSymbol> notExistingSymbolEnclosingScope = scope.resolvePackage("WrongName Example");

    assertTrue(packageSymbolEnclosingScope.isPresent());
    assertTrue(blockSymbolEnclosingScope.isPresent());
    assertTrue(valueTypeSymbolEnclosingScope.isPresent());
    assertFalse(notExistingSymbolEnclosingScope.isPresent());
  }


  @Test
  public void testSuccessfulCreationInMultipleFiles() {
    String currentPath = this.pathToOfficialSysMLTrainingExamples;
    List<ASTUnit> models = SysMLTool.parseDirectory(currentPath);
    SysMLGlobalScope topScope = SysMLTool.buildSymbolTable(currentPath, models);

    //Testing Symboltable
    Optional<PackageSymbol> packageSymbolBlocksExample = topScope.resolvePackage("Blocks Example");
    assertTrue(packageSymbolBlocksExample.isPresent());
    Optional<PackageSymbol> packageSymbolCommentExample = topScope.resolvePackage("Comment Example");
    assertTrue(packageSymbolCommentExample.isPresent());
    Optional<PackageSymbol> packageSymbolPackageExample = topScope.resolvePackage("Package Example");
    assertTrue(packageSymbolPackageExample.isPresent());

    Optional<PackageSymbol> notExistingPackageSymbol = topScope.resolvePackage("WrongName...!");
    assertFalse(notExistingPackageSymbol.isPresent());

  }
}
