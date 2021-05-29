/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTRootNamespace;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.sysml.SysMLMill;
import de.monticore.lang.sysml.sysml._symboltable.*;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
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
    ASTUnit astUnit =  this.parseSysMLSingleModel(currentPath);
    ASTRootNamespace rootNamespace  = (ASTRootNamespace) astUnit;
    Log.debug("Package name is " + rootNamespace.getPackage(0).getName(), this.getClass().getName());
    assertEquals("Blocks Example", rootNamespace.getPackage(0).getName());
    ASTBlock block = (ASTBlock) rootNamespace.getPackage(0).getPackageBody().getPackageMember(0).getPackagedDefinitionMember();
    Log.debug("Block name is " + block.getName(), this.getClass().getName());
    assertEquals("Blocks Example", rootNamespace.getPackage(0).getName());

    //Creating Symboltable
    ModelPath mp = SysMLTool.createModelpath(currentPath);
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    ISysMLArtifactScope topScope = helperSysMLSymbolTableCreator.createSymboltableSingleASTUnit(astUnit, mp);

    //Testing Symboltable
    // Optional<PackageSymbol> packageSymbol = topScope.resolvePackage("Blocks Example");
    Optional<SysMLTypeSymbol> packageSymbol = topScope.resolveSysMLType("Blocks Example");
    // Optional<BlockSymbol> blockSymbol = topScope.getSubScopes().get(0).resolveBlockDown("Vehicle");
    Optional<SysMLTypeSymbol> blockSymbol = topScope.getSubScopes().get(0).resolveSysMLType("Vehicle");
    Optional<SysMLTypeSymbol> valueTypeSymbol = topScope.getSubScopes().get(0).resolveSysMLType(
        "VehicleStatus");
    Optional<SysMLTypeSymbol> notExistingSymbol = topScope.resolveSysMLType("WrongName Example");

    assertTrue(packageSymbol.isPresent());
    assertEquals("Blocks Example", packageSymbol.get().getName());
    assertTrue(blockSymbol.isPresent());
    assertEquals("Vehicle", blockSymbol.get().getName());
    assertTrue(valueTypeSymbol.isPresent());
    assertEquals("VehicleStatus", valueTypeSymbol.get().getName());
    assertFalse(notExistingSymbol.isPresent());


    SysMLArtifactScope scope = (SysMLArtifactScope) astUnit.getEnclosingScope();
    //Testing resolving with astUnit
    Optional<SysMLTypeSymbol> packageSymbolEnclosingScope = scope.resolveSysMLType("Blocks Example");
    Optional<SysMLTypeSymbol> blockSymbolEnclosingScope = scope.getSubScopes().get(0).resolveSysMLType("Vehicle");
    Optional<SysMLTypeSymbol> valueTypeSymbolEnclosingScope = scope.getSubScopes().get(0).resolveSysMLType(
        "VehicleStatus");
    Optional<SysMLTypeSymbol> notExistingSymbolEnclosingScope = scope.resolveSysMLType("WrongName Example");

    assertTrue(packageSymbolEnclosingScope.isPresent());
    assertTrue(blockSymbolEnclosingScope.isPresent());
    assertTrue(valueTypeSymbolEnclosingScope.isPresent());
    assertFalse(notExistingSymbolEnclosingScope.isPresent());
  }

  @Test
  public void testSuccessfulCreationInMultipleFiles() {
    SysMLMill.globalScope().clear();
    String currentPath = this.pathToOfficialSysMLTrainingExamples;
    List<ASTUnit> models = SysMLTool.parseDirectory(currentPath);
    ISysMLGlobalScope topScope = SysMLTool.buildSymbolTable(currentPath, models);

    //Testing Symboltable
    Optional<SysMLTypeSymbol> packageSymbolBlocksExample = topScope.resolveSysMLType("Blocks Example");
    assertTrue(packageSymbolBlocksExample.isPresent());
    Optional<SysMLTypeSymbol> packageSymbolCommentExample = topScope.resolveSysMLType("Comment Example");
    assertTrue(packageSymbolCommentExample.isPresent());
    Optional<SysMLTypeSymbol> packageSymbolPackageExample = topScope.resolveSysMLType("Package Example");
    assertTrue(packageSymbolPackageExample.isPresent());

    Optional<SysMLTypeSymbol> notExistingPackageSymbol = topScope.resolveSysMLType("WrongName...!");
    assertFalse(notExistingPackageSymbol.isPresent());

  }
}
