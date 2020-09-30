package de.monticore.lang.sysml._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.basics.sysmldefault.importsandpackages._symboltable.PackageSymbol;
import de.monticore.lang.sysml.bdd._symboltable.BlockSymbol;
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
  @Ignore
  @Test
  public void testSuccessfulCreationInOneFile() { //TODO
    String currentPath = this.pathToOfficialSysMLTrainingExamples + "/02. Blocks/Blocks Example.sysml";

    //Ensure successful parsing
    ASTUnit astUnit =  this.parseSysMLSingleModel(currentPath);
    ASTPackageUnit packageUnit  = (ASTPackageUnit) astUnit;
    System.out.println("Name is " + packageUnit.getPackage().getName());
    assertEquals("Blocks Example", packageUnit.getPackage().getName());

    ModelPath mp = SysMLTool.createModelpath(currentPath);
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    SysMLGlobalScope topScope = helperSysMLSymbolTableCreator.createSymboltable(mp);
    Optional<PackageSymbol> packageSymbol = topScope.resolvePackage("Blocks Example");
    if (packageSymbol.isPresent()) {
      System.out.println("Resolved package symbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName());
      Log.info("Resolved package symbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName(),
          SymbolTableCreationTest.class.getName());
    }else {
      System.out.println("Resolving was not successful.");
    }

    Optional<BlockSymbol> blockSymbol = topScope.resolveBlock("Vehicle");
    if (blockSymbol.isPresent()) {
      System.out.println("Resolved blockSymbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName());
      Log.info("Resolved block symbol \"Blocks Example\"; ResolvedName = " + packageSymbol.get().getName(),
          SymbolTableCreationTest.class.getName());
    }else {
      System.out.println("Resolving was not successful.");
    }

    Optional<BlockSymbol> notExistingSymbol = topScope.resolveBlock("WrongName");

    assertTrue(packageSymbol.isPresent());
    assertTrue(blockSymbol.isPresent());
    assertFalse(notExistingSymbol.isPresent());

    // SysMLCommonSymbolTableCreator symbolTableCreator = new SysMLCommonSymbolTableCreator();
    //SysMLSymbolTableCreator symbolTableCreator = new SysMLSymbolTableCreator(astUnit);
  }
}
