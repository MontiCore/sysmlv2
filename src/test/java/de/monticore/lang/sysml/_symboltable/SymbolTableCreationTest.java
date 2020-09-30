package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageUnit;
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

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SymbolTableCreationTest extends AbstractSysMLTest {
  @Test
  public void testSuccessfulCreationInOneFile() { //TODO
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
    if (packageSymbol.isPresent()) {
      System.out.println("Resolved package symbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName());
      Log.info("Resolved package symbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName(),
          SymbolTableCreationTest.class.getName());
    }else {
      System.out.println("Resolving Package was not successful.");
    }

    /* TODO
    Optional<BlockSymbol> blockSymbol = topScope.resolveBlockDown("Vehicle");
    if (blockSymbol.isPresent()) {
      System.out.println("Resolved blockSymbol \"Vehicle\"; ResolvedName = " + packageSymbol.get().getName());
      Log.info("Resolved block symbol \"Vehicle\"; ResolvedName = " + packageSymbol.get().getName(),
          SymbolTableCreationTest.class.getName());
    }else {
      System.out.println("Resolving Block was not successful.");
    }


    Optional<ValueTypeStdSymbol> valueTypeSymbol = topScope.resolveValueTypeStdDown("VehicleStatus");
    if (valueTypeSymbol.isPresent()) {
      System.out.println("Resolved valueTypeSymbol \"VehicleStatus\"; ResolvedName = " + packageSymbol.get().getName());
      Log.info("Resolved valueTypeSymbol \"VehicleStatus\"; ResolvedName = " + packageSymbol.get().getName(),
          SymbolTableCreationTest.class.getName());
    }else {
      System.out.println("Resolving VehicleStatus was not successful.");
    }*/

    Optional<PackageSymbol> notExistingSymbol = topScope.resolvePackage("WrongName Example");

    assertTrue(packageSymbol.isPresent());
    // TODO assertTrue(blockSymbol.isPresent());
    // TODO assertTrue(valueTypeSymbol.isPresent());
    assertFalse(notExistingSymbol.isPresent());

    // SysMLCommonSymbolTableCreator symbolTableCreator = new SysMLCommonSymbolTableCreator();
    //SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(astUnit);
  }
}
