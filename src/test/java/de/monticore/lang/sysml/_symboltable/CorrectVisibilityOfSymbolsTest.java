package de.monticore.lang.sysml._symboltable;

import com.sun.xml.internal.bind.v2.TODO;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._symboltable.ISysMLImportsAndPackagesScope;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class CorrectVisibilityOfSymbolsTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  public void testIfVisibilityIsConsidered() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/visibilityOfSymbols");
    //System.out.println("Parsed and build, now testing.");
    //Checking Resolving with a name without ASTQualifiedName
    assertTrue(models.size() != 0);
    List<String> packageWithImportsQN = new ArrayList();
    /* Vehicles{
	      package Automobile{
		        package ModellY{*/
    packageWithImportsQN.add("Vehicles");
    packageWithImportsQN.add("Automobile");
    packageWithImportsQN.add("ModellY");
    List<SysMLTypeSymbol> packageWithImportsSymbol = ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(packageWithImportsQN, models.get(0).getEnclosingScope().getEnclosingScope());

    assertTrue(packageWithImportsSymbol.size() == 1);
    assertTrue(packageWithImportsSymbol.get(0).getAstNode() instanceof ASTPackage);
    ASTPackage packageWithImports = (ASTPackage) packageWithImportsSymbol.get(0).getAstNode();

    ISysMLImportsAndPackagesScope scopeToLookIn  = packageWithImports.getPackageBody().getSpannedScope();

    assert(!scopeToLookIn.resolveSysMLType("VehiclePrivate").isPresent());
    assert(scopeToLookIn.resolveSysMLType("BusPublic").isPresent());
  }


  @Test
  public void testIfRexportWorksWithCorrectVisibility() {
    //System.out.println("Starting");
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/reexportImports");
    //System.out.println("Parsed and build, now testing.");
    //Checking Resolving with a name without ASTQualifiedName
    assertTrue(models.size() != 0);
    List<String> packageWithImportsQN = new ArrayList();
//    Vehicles{
//	      package Automobile{
//		        package ModellY{
    packageWithImportsQN.add("Vehicles");
    packageWithImportsQN.add("Automobile");
    packageWithImportsQN.add("ModellY");
    List<SysMLTypeSymbol> packageWithImportsSymbol = ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(packageWithImportsQN, models.get(0).getEnclosingScope().getEnclosingScope());

    assertTrue(packageWithImportsSymbol.size() == 1);
    assertTrue(packageWithImportsSymbol.get(0).getAstNode() instanceof ASTPackage);
    ASTPackage packageWithImports = (ASTPackage) packageWithImportsSymbol.get(0).getAstNode();


//			The symbols in 'Symbol DefinitionNotTransitevlyImported' should not be in scope because thy are a private import
//			in Symbol Definition2
//			block VehicleDef1;
//      block BusDef1;
//      // import 'Symbol Definition'::Vehicle; //should be public => part of scope
//      // import 'Symbol Definition'; //should be public => part of scope
//      // import 'Symbol Definition2'::VehiclePrivate; //should be private => not part of scope


    ISysMLImportsAndPackagesScope scopeToLookIn  = packageWithImports.getPackageBody().getSpannedScope();

    assert(scopeToLookIn.resolveSysMLType("DefinedInScope").isPresent());
    assert(scopeToLookIn.resolveSysMLType("BusPublic").isPresent());
    assert(scopeToLookIn.resolveSysMLType("Vehicle").isPresent());
    assert(scopeToLookIn.resolveSysMLType("Bus").isPresent());

    assert(!scopeToLookIn.resolveSysMLType("VehiclePrivate").isPresent());
    assert(!scopeToLookIn.resolveSysMLType("VehicleDef1").isPresent());
    assert(!scopeToLookIn.resolveSysMLType("BusDef1").isPresent());
  }
}
