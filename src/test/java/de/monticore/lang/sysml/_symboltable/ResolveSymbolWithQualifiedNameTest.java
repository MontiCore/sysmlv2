package de.monticore.lang.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.utils.AbstractSysMLTest;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ResolveSymbolWithQualifiedNameTest extends AbstractSysMLTest {
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws RecognitionException {
    this.setUpLog();
  }

  @Test
  public void testResolveQNInOneFileWithSpaces() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/qualifiedNameWithSpaces");

    //Checking Resolving with a name without ASTQualifiedName
    assertTrue(models.size() != 0);
    List<String> packageWithImportsQN = new ArrayList();
    /* Vehicles{
	      package Automobile{
		        package ModellY{*/
    packageWithImportsQN.add("Vehicles");
    packageWithImportsQN.add("Automobile Luxus");
    packageWithImportsQN.add("Modell Y");
    List<SysMLTypeSymbol> packageWithImportsSymbol = ResolveQualifiedNameHelper.
        resolveQualifiedNameAsListInASpecificScope(packageWithImportsQN,
            models.get(0).getEnclosingScope().getEnclosingScope());

    assertTrue(packageWithImportsSymbol.size() == 1);
  }

  @Test
  public void testCannotResolveQNInOneFileWithSpaces() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/qualifiedNameWithSpaces");

    //Checking Resolving with a name without ASTQualifiedName
    assertTrue(models.size() != 0);
    List<String> packageWithImportsQN = new ArrayList();
    /* Vehicles{
	      package Automobile{
		        package ModellY{*/
    packageWithImportsQN.add("VehiclesWrong");
    packageWithImportsQN.add("Automobile Luxus");
    packageWithImportsQN.add("Modell Y");
    List<SysMLTypeSymbol> packageWithImportsSymbol =
        ResolveQualifiedNameHelper.resolveQualifiedNameAsListInASpecificScope(packageWithImportsQN,
            models.get(0).getEnclosingScope().getEnclosingScope());

    assertTrue(packageWithImportsSymbol.size() == 0);
  }

  @Test
  public void testComplexResolveQNInOneFile() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/complexQualifiedName");

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

    //Resolve Imports.
    for (int i = 0; i < 5; i++) {
      ASTPackagedDefinitionMember importMember =
          (packageWithImports.getPackageBody().getPackageMember(i).getPackagedDefinitionMember());

      assertTrue(importMember instanceof ASTAliasPackagedDefinitionMember);
      ASTAliasPackagedDefinitionMember importStatement = (ASTAliasPackagedDefinitionMember) importMember;
      List<SysMLTypeSymbol> resolvedTypes = importStatement.getQualifiedName().resolveSymbols();
      assertTrue(resolvedTypes.size() == 0 || resolvedTypes.size() == 1);

      //For debugging:
      /*System.out.println("Resolving " + importStatement.getQualifiedName().getFullQualifiedName() +
          " leads to resolving to " + resolvedTypes.size() + " resolved Symbols.");
      if(resolvedTypes.size() == 1){
        System.out.println(i + " --- The symbol from was in scope " + resolvedTypes.get(0).getAstNode().getEnclosingScope().getName()+
            " ,which was in scope " + resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
      }*/

      switch (i) {
        /*
        0: import Planes::ModellX; //Should be in scope Planes and then scope Automobile
        1: import Vehicles::Planes::ModellX; //Should be in scope Planes and the Vehicles
        2: import 'Symbol Definition'::Vehicle; //should resolve (to other file)
        3: import 'Symbol Definition'; //Should resolve (to other file)
        4: import NotDefined ; //Should not resolve.*/

        case 0:
          assertEquals(1, resolvedTypes.size());
          assertEquals("Planes", resolvedTypes.get(0).getAstNode().getEnclosingScope().getName());
          assertEquals("Automobile", resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
          break;
        case 1:
          assertEquals(1, resolvedTypes.size());
          assertEquals("Planes", resolvedTypes.get(0).getAstNode().getEnclosingScope().getName());
          assertEquals("Vehicles", resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
          break;
        case 2:
        case 3:
          assertEquals(1, resolvedTypes.size());
          break;
        case 4:
          assertEquals(0, resolvedTypes.size());
          break;
        default:
          assertTrue(false); //This should not happen.
      }

    }
  }
  @Test
  public void testComplexResolveQNInTwoFiles() {
    List<ASTUnit> models = this.validParseAndBuildSymbolsInSubDir("/imports/complexQualifiedNameTwoFiles");

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

    //Resolve Imports.
    for (int i = 0; i < 5; i++) {
      ASTPackagedDefinitionMember importMember =
          (packageWithImports.getPackageBody().getPackageMember(i).getPackagedDefinitionMember());

      assertTrue(importMember instanceof ASTAliasPackagedDefinitionMember);
      ASTAliasPackagedDefinitionMember importStatement = (ASTAliasPackagedDefinitionMember) importMember;
      List<SysMLTypeSymbol> resolvedTypes = importStatement.getQualifiedName().resolveSymbols();
      assertTrue(resolvedTypes.size() == 0 || resolvedTypes.size() == 1);

      //For debugging:
      /*System.out.println("Resolving " + importStatement.getQualifiedName().getFullQualifiedName() +
          " leads to resolving to " + resolvedTypes.size() + " resolved Symbols.");
      if(resolvedTypes.size() == 1){
        System.out.println(i + " --- The symbol from was in scope " + resolvedTypes.get(0).getAstNode().getEnclosingScope().getName()+
            " ,which was in scope " + resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
      }*/

      switch (i) {
        /*
        0: import Planes::ModellX; //Should be in scope Planes and then scope Automobile
        1: import Vehicles::Planes::ModellX; //Should be in scope Planes and the Vehicles
        2: import 'Symbol Definition'::Vehicle; //should resolve (to other file)
        3: import 'Symbol Definition'; //Should resolve (to other file)
        4: import NotDefined ; //Should not resolve.*/

        case 0:
          assertEquals(1, resolvedTypes.size());
          assertEquals("Planes", resolvedTypes.get(0).getAstNode().getEnclosingScope().getName());
          assertEquals("Automobile", resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
          break;
        case 1:
          assertEquals(1, resolvedTypes.size());
          assertEquals("Planes", resolvedTypes.get(0).getAstNode().getEnclosingScope().getName());
          assertEquals("Vehicles", resolvedTypes.get(0).getAstNode().getEnclosingScope().getEnclosingScope().getName());
          break;
        case 2:
        case 3:
          assertEquals(1, resolvedTypes.size());
          break;
        case 4:
          assertEquals(0, resolvedTypes.size());
          break;
        default:
          assertTrue(false); //This should not happen.
      }

    }
  }
}
