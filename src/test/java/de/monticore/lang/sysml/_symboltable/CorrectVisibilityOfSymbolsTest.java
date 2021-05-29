/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._symboltable.ISysMLImportsAndPackagesScope;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.common.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTClassifierDeclarationCompletion;
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
  @Ignore // TODO fix me
  public void testIfVisibilityIsConsideredInImport() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/visibilityOfSymbols");
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
  @Ignore // TODO fix me
  public void testIfRexportWorksWithCorrectVisibility() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/reexportImports");
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


  @Test
  @Ignore // TODO fix me
  public void  testQualifiedNameVisibility(){
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/qualifiedNameVisibility");
    //System.out.println("Parsed and build, now testing.");
    //Checking Resolving with a name without ASTQualifiedName
    assertTrue(models.size() != 0);
    List<String> blockAQN = new ArrayList();
    blockAQN.add("Vehicles");
    blockAQN.add("useOfDefs");
    blockAQN.add("a");
    List<SysMLTypeSymbol> blockA =
        ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(blockAQN,
            models.get(0).getEnclosingScope().getEnclosingScope());
    assertTrue(blockA.size() ==1);
    List<String> blockAprivateQN = new ArrayList();
    blockAprivateQN.add("Vehicles");
    blockAprivateQN.add("useOfDefs");
    blockAprivateQN.add("aPrivate");
    List<SysMLTypeSymbol> blockAprivate =
        ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(blockAprivateQN,
            models.get(0).getEnclosingScope().getEnclosingScope());
    assertTrue(blockAprivate.size() ==1);

    assertTrue(blockA.get(0).getAstNode() instanceof ASTBlock);
    assertTrue(blockAprivate.get(0).getAstNode() instanceof ASTBlock);
    ASTBlock blockACasted = (ASTBlock)blockA.get(0).getAstNode();
    ASTBlock blockAPrivatedCasted = (ASTBlock)blockAprivate.get(0).getAstNode();

    ASTClassifierDeclarationCompletion classifierA =
        (blockACasted.getBlockDeclaration().getClassifierDeclarationCompletion());

    assertTrue(classifierA instanceof ASTClassifierDeclarationCompletionStd);
    assertEquals(1,
        ((ASTClassifierDeclarationCompletionStd )classifierA).getSuperclassingList().getQualifiedName(0).resolveSymbols().size());

    ASTClassifierDeclarationCompletion classifierAPrivate =
        (blockAPrivatedCasted.getBlockDeclaration().getClassifierDeclarationCompletion());

    assertTrue(classifierAPrivate instanceof ASTClassifierDeclarationCompletionStd);
    assertEquals(0,
        ((ASTClassifierDeclarationCompletionStd )classifierAPrivate).getSuperclassingList().getQualifiedName(0).resolveSymbols().size());

  }


  @Test
  public void testIfCyclicImportsLeadToInfiniyLoop() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/cyclicImport");
    assertTrue(models.size() != 0);
  }
}
