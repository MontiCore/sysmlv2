package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.SysMLTool;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.collections.AST;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
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
  public void resolveToOtherScope(){

    String modelPath = this.pathToValidModels + "/scopes/simple";
    List<ASTUnit> models = SysMLTool.parseDirectory(modelPath);
    SysMLGlobalScope topScope = SysMLTool.buildSymbolTable(modelPath, models);
    Optional<SysMLTypeSymbol> packageWithImport = topScope.resolveSysMLType("Import Vehicle");
    /*for(ASTUnit model : models){
      if(model instanceof ASTPackage){
        Log.debug("Parsed model " + ((ASTPackage) model).getName(), this.getClass().getName());
        if(((ASTPackage) model).getName().equals("Import Vehicle")){
          packageWithImport = Optional.of((ASTPackage) model);
        }
      }
    }*/
    assertTrue(packageWithImport.isPresent());
    Optional<SysMLTypeSymbol> vehicleSym =
        ((ASTPackage)packageWithImport.get().getAstNode())
            .getPackageBody().getSpannedScope().resolveSysMLType("Vehicle");
    //TODO check if this is the correct scope
    //this.printAllFindings();
    assertTrue(vehicleSym.isPresent());
    //assertEquals(0, Log.getFindings());
  }
}
