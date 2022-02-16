package schrotttests;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTRootNamespace;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTNestedUsageMemberStd;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTStructureUsageMember;
import de.monticore.lang.sysml.stm._ast.ASTBehaviorUsageMemberStateUsage;
import de.monticore.lang.sysml.stm._ast.ASTStateUsage;
import de.monticore.lang.sysml4verification.SysML4VerificationLanguage;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTBlock;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import de.monticore.lang.sysml4verification._symboltable.BlockSymbol;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationArtifactScope;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationGlobalScope;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysml4verification._symboltable.PortDefinitionStdSymbol;
import de.monticore.lang.sysml4verification._symboltable.StateDefinitionSymbol;
import de.monticore.lang.sysml4verification._symboltable.SysML4VerificationScope;
import de.monticore.lang.sysml4verification._symboltable.SysML4VerificationSymbolTableCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SymboltableTest {

  @Test
  public void testSymbolTable() throws IOException {

    //parse model (Inverter.sysml)
    Optional<ASTUnit> optAstInverter = new SysML4VerificationParser().parse(
        "src/test/resources/simple/automaton/" + "Inverter" + ".sysml");
    //check if parsing was successful
    assertThat(optAstInverter).isNotEmpty();
    //parse model (DoubleInverter.sysml)
    Optional<ASTUnit> optAstDoubleInverter = new SysML4VerificationParser().parse(
        "src/test/resources/simple/automaton/" + "DoubleInverter" + ".sysml");
    //check if parsing was successful
    assertThat(optAstDoubleInverter).isNotEmpty();

    // initialise symbol table
    new SysML4VerificationSymbolTableCreator().createSymboltable(Arrays.asList(
        optAstDoubleInverter.get(), optAstInverter.get()),
        new ModelPath(Paths.get("src/test/resources/simple/automaton")));

    // resolve port type used by DoubleInverter (DoubleInverter.sysml), which is defined in Inverter.sysml.
    // By this, resolving in subscopes is tested
    ASTRootNamespace astDoubleInverter = (ASTRootNamespace) optAstDoubleInverter.get();
    // get Double Inverter part def
    ASTBlock doubleInverter = (ASTBlock) astDoubleInverter.getPackage(0).getPackageBody().getPackageMember(0)
        .getPackagedDefinitionMember();
    // get input port of double inverter
    ASTStructureUsageMember portInput = ((ASTNestedUsageMemberStd) ((ASTDefinitionBodyStd) doubleInverter.getDefinitionBody())
        .getNestedUsageMember(0)).getStructureUsageMember();
    assertThat(portInput.isPresentPortMember());
    // resolve to get the corresponding port definition from the subscope
    Optional<PortDefinitionStdSymbol> resolvedPortType = ((SysML4VerificationScope) portInput.getEnclosingScope())
        .resolvePortDefinitionStd("BooleanInput");
    // check if resolved successfully
    assertThat(resolvedPortType).isPresent();

    // get Inverter part def
    ASTRootNamespace astInverter = (ASTRootNamespace) optAstInverter.get();
    ASTBlock inverter = (ASTBlock) astInverter.getPackage(0).getPackageBody().getPackageMember(
        2).getPackagedDefinitionMember();
    //resolve state definition of inverter
    ASTStateUsage state = ((ASTBehaviorUsageMemberStateUsage) ((ASTNestedUsageMemberStd) ((ASTDefinitionBodyStd) inverter
        .getDefinitionBody()).getNestedUsageMember(
        2)).getBehaviorUsageMember()).getStateUsage();
    Optional<StateDefinitionSymbol> resolvedStateDefinition = ((SysML4VerificationScope) state.getEnclosingScope())
        .resolveStateDefinition("AutomatonInverter");
    // check if resolved successfully
    assertThat(resolvedStateDefinition).isPresent();
  }

  /**
   * Dieser Test versucht u.a Symbole zu resolven, welche in einer anderen SysML file definiert sind.
   * (Dadurch wird die korrekte Bennenung der subscopes getestet.)
   */
  @Test
  public void testSymbolTableFQN() throws IOException {

    // use Language class (parsing + symboltable)
    ISysML4VerificationGlobalScope globalScope = SysML4VerificationLanguage.getGlobalScopeFor(
        Path.of("src/test/resources/simple/example/"));

    //resolve BlockA. BlockA will be used following as the context for more symbol resolutions
    ASTBlock blockA = globalScope.resolveBlock("example.BlockA").get().getAstNode();
    //resolve port def (Port.sysml) from local context of blockA (Block.sysml)
    ASTStructureUsageMember portI = ((ASTNestedUsageMemberStd) ((ASTDefinitionBodyStd) blockA.getDefinitionBody())
        .getNestedUsageMember(0)).getStructureUsageMember();
    // resolve (with package name)
    SysML4VerificationScope scope = (SysML4VerificationScope) portI.getEnclosingScope();
    Optional<PortDefinitionStdSymbol> resolvedPortType = scope
        .resolvePortDefinitionStd("example.BooleanInput");
    // check if resolved successfully
    assertThat(resolvedPortType).isPresent();
    // resolve (without package name - should also work)
    // only one symbol should be found. The port in PortOtherPackage.sysml must not be found.
    resolvedPortType = scope.resolvePortDefinitionStd("BooleanInput");
    // check if resolved successfully
    assertThat(resolvedPortType).isPresent();

    // resolve down (but stay in same package) - same file
    Optional<BlockSymbol> resolved = blockA.getEnclosingScope().resolveBlock(
        "BlockB");
    assertThat(resolved).isEmpty();

    // resolve up - same file
    resolved = ((SysML4VerificationScope) portI.getEnclosingScope()).resolveBlock(
        "BlockA");
    assertThat(resolved).isPresent();

    ASTBlock blockC = globalScope.resolveBlock("example.example2.BlockC").get().getAstNode();
    // resolve up (and in other files) from context of portJ
    ASTStructureUsageMember portJ = ((ASTNestedUsageMemberStd) ((ASTDefinitionBodyStd) blockC.getDefinitionBody())
        .getNestedUsageMember(0)).getStructureUsageMember();
    scope = (SysML4VerificationScope) portJ.getEnclosingScope();
    resolvedPortType = scope.resolvePortDefinitionStd("example2.BooleanInput2");
    // check if resolved successfully
    assertThat(resolvedPortType).isPresent();

    // resolve up (and in other files)
    ASTStructureUsageMember portK = ((ASTNestedUsageMemberStd) ((ASTDefinitionBodyStd) blockC.getDefinitionBody())
        .getNestedUsageMember(1)).getStructureUsageMember();
    scope = (SysML4VerificationScope) portK.getEnclosingScope();
    resolvedPortType = scope.resolvePortDefinitionStd("BooleanInput3");
    // check if resolved successfully
    assertThat(resolvedPortType).isPresent();

  }

  @Test
  public void testNestedPackages() throws IOException {

    SysML4VerificationMill.init();

    Optional<ASTUnit> optAst = new SysML4VerificationParser().parse(
        "src/test/resources/simple/nested/NestedPackages.sysml");

    // clear symboltable to avoid loading nestedPackages multiple times
    SysML4VerificationMill.globalScope().clear();
    ISysML4VerificationArtifactScope artifactScope = SysML4VerificationMill.scopesGenitorDelegator().createFromAST(
        optAst.get());

    // resolve an arbitrary block to test resolution without artifact scope names
    Optional<BlockSymbol> blockSymbol = artifactScope.resolveBlock("Package_Top.Package_B.B");

    assertThat(blockSymbol).isPresent();
    // check if artifact scope was set erroneously
    assertThat(artifactScope.isPresentName()).isFalse();

    // check if both top-level scopes have the appropriate names
    ISysML4VerificationScope package_Top = artifactScope.getSubScopes().get(0);
    assertThat(package_Top.isPresentName()).isTrue();
    assertThat(package_Top.getName()).isEqualTo("Package_Top");

    ISysML4VerificationScope package_Top2 = artifactScope.getSubScopes().get(1);
    assertThat(package_Top2.isPresentName()).isTrue();
    assertThat(package_Top2.getName()).isEqualTo("Package_Top2");

    // check if deepest nested package has the appropriate name
    ISysML4VerificationScope package_BA = package_Top.getSubScopes().get(1).getSubScopes().get(0);
    assertThat(package_BA.isPresentName()).isTrue();
    assertThat(package_BA.getName()).isEqualTo("Package_BA");

    // check if block symbol on deepest level is present in symboltable
    BlockSymbol blockBA = package_BA.getBlockSymbols().values().get(0);
    assertThat(blockBA.getName()).isEqualTo("BA");
  }

  @Test
  public void testRefinement() throws IOException {
    // parse models
    Optional<ASTUnit> clockAst = new SysML4VerificationParser().parse(
        "src/test/resources/pfs/hlrprime/Clock.sysml");
    Optional<ASTUnit> fairClockAst = new SysML4VerificationParser().parse(
        "src/test/resources/pfs/hlr/FairClock.sysml");
    Optional<ASTUnit> fairClockInvalidAst = new SysML4VerificationParser().parse(
        "src/test/resources/pfs/hlr/FairClockInvalid.sysml");

    // create symbol table
    ArrayList<ASTUnit> models = new ArrayList<>();
    models.add(clockAst.get());
    models.add(fairClockAst.get());
    models.add(fairClockInvalidAst.get());

    final ModelPath mp = new ModelPath(Paths.get("src/test/resources/pfs"));
    ISysML4VerificationGlobalScope scope = new SysML4VerificationSymbolTableCreator().createSymboltable(models, mp);

    // check valid refinement, symbol must resolve
    ASTPackagedDefinitionMember astBlock = ((ASTRootNamespace) fairClockAst.get()).getPackage(0).getPackageBody()
        .getPackageMember(2).getPackagedDefinitionMember();
    assertThat(astBlock.getEnclosingScope()).isNotNull();
    SysMLTypeSymbol clockSym = ((SysML4VerificationScope) astBlock.getEnclosingScope()).getSysMLTypeSymbols().get(
        "Clock").get(0);
    assertThat(clockSym).isNotNull();
    List<SysMLTypeSymbol> refinements = ((ASTBlock) astBlock).getRefinements();
    assertThat(refinements.size()).isGreaterThan(0);

    // check invalid refinement, symbol must not resolve
    astBlock = ((ASTRootNamespace) fairClockInvalidAst.get()).getPackage(0).getPackageBody()
        .getPackageMember(2).getPackagedDefinitionMember();
    assertThat(astBlock.getEnclosingScope()).isNotNull();
    List<SysMLTypeSymbol> unfairClockSym = ((SysML4VerificationScope) astBlock.getEnclosingScope()).getSysMLTypeSymbols().get(
        "UnfairClock");
    assertThat(unfairClockSym.size()).isEqualTo(0);
    refinements = ((ASTBlock) astBlock).getRefinements();
    assertThat(refinements.size()).isEqualTo(0);

    // coco check
    // TODO This test needs some reworking
    // SysML4VerificationCoCoChecker.init().checkAll(fairClockAst.get());
  }

  @Test
  public void testResolutionWithoutImports() throws IOException {
    Optional<ASTUnit> optAst = new SysML4VerificationParser().parse(
        "src/test/resources/simple/nested/NestedPackages.sysml");

    var globalScope = SysML4VerificationLanguage.getGlobalScopeFor(Paths.get("src/test/resources/simple/nested"));
    ISysML4VerificationArtifactScope artifactScope = (ISysML4VerificationArtifactScope) globalScope.getSubScopes().get(
        0);
    ISysML4VerificationScope package_BA = artifactScope.getSubScopes().get(0).getSubScopes().get(1).getSubScopes().get(
        0);

    // trials

    // this block should not be resolved without package qualifier. Does only top-down as we resolve from artifact scope
    Optional<BlockSymbol> wrongQualification = artifactScope.resolveBlock("A");
    assertThat(wrongQualification).isEmpty();

    // FQN of symbol in uncle package resolves during top-down
    Optional<BlockSymbol> fqnSymbolFromUncle = package_BA.resolveBlock("Package_Top.Package_A.A");
    assertThat(fqnSymbolFromUncle).isPresent();

    // Package symbol for the uncle package resolves resolves during bottom-up
    Optional<SysMLTypeSymbol> relativeUnclePackageSymbol = package_BA.resolveSysMLType("Package_A");
    assertThat(relativeUnclePackageSymbol).isPresent();

    // Block symbol from uncle package does not resolve. Should resolve during bottom-up.
    Optional<BlockSymbol> relativeSymbolFromUncle = package_BA.resolveBlock("Package_A.A");
    assertThat(relativeSymbolFromUncle).isPresent();

    // the fqn symbol and the relative symbol should be the same
    assertThat(relativeSymbolFromUncle).isEqualTo(fqnSymbolFromUncle);

  }
}
