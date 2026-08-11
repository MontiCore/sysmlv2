package symboltable;

import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2DeSer;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.symboltable.completers.ImportsCompleter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolTOP;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportResolveTest extends NervigeSymboltableTests {

  /**
   * Tests the general resolution of FQN without using imports.
   * That is the main downstream feature the import-tests rely on, because
   * while processing the resolve by imports we do try to resolve their FQNs.
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testFQNResolving() throws IOException {
    var parentModel = "package Other { part def Parent; }";
    var model = "part def Child : Other.Parent;";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ast.getSysMLElement(0);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.printType();

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolvePartDef(parentName);

    assertThat(optParent).isPresent();
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Tests direct SysML Import "Other::Parent" on root level of the
   * SysML-Model / resolve through ArtifactsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testArtifactsScopeImport() throws IOException {
    var parentModel = "package Other { part def Parent; }";
    var model = "private import Other::Parent; part def Child : Parent;";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ast.getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Tests star/wildcard SysML Import "Other::*" on root level of the
   * SysML-Model / resolve through ArtifactsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testArtifactsStarImport() throws IOException {
    var parentModel = "package Other { part def Parent; }";
    var model = "private import Other::*; part def Child : Parent;";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ast.getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Recursive Imports are currently not handled by our implementation.
   * Tests recursive SysML Import "Other::**" on root level of the
   * SysML-Model / resolve through ArtifactsScope.
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testArtifactsScopeRecursiveImport() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "private import Other::**; part def Child : Parent;";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ast.getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

  @Test()
  public void testArtifactScopeBothSidesImport() throws IOException {
    var parentModel = " part def Parent { part def Daughter { part def DaughterDaughter; } } part def Uncle; ";
    var model = "private import Parent::**; part def ParentClone :> Parent; part def DaughterClone :> Daughter;"
        + " part def DaughterDaughterClone :> DaughterDaughter; ";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ast.getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Parent");

    var partDefDaughter = (ASTPartDef) ast.getSysMLElement(2);
    var parentRefDaughter = (ASTMCQualifiedType) partDefDaughter.getSpecialization(0).getSuperTypes(0);

    var daughterName = parentRefDaughter.getNameList().get(0);

    var optDaughter = ((ISysMLv2Scope) parentRefDaughter.getEnclosingScope()).resolveType(daughterName);

    assertThat(optDaughter).isPresent(); // check if we did resolve
    assertThat(optDaughter.get().getFullName()).isEqualTo("Parent.Daughter");

    var partDefDaughterDaughter = (ASTPartDef) ast.getSysMLElement(3);
    var parentRefDaughterDaughter = (ASTMCQualifiedType) partDefDaughterDaughter.getSpecialization(0).getSuperTypes(0);

    var daughterDaughterName = parentRefDaughterDaughter.getNameList().get(0);

    var optDaughterDaughter = ((ISysMLv2Scope) parentRefDaughterDaughter.getEnclosingScope()).resolveType(daughterDaughterName);

    assertThat(optDaughterDaughter).isPresent(); // check if we did resolve
    assertThat(optDaughterDaughter.get().getFullName()).isEqualTo("Parent.Daughter.DaughterDaughter");
  }

  /**
   * Tests direct SysML Import "Other::Parent" within a SysML-Namespace /
   * resolve through SysMLsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testSysMLScopeImport() throws IOException {
    var parentModel = "package Other { part def Parent; }";
    var model = "package test { private import Other::Parent; part def Child : Parent; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Tests star/wildcard SysML "Other::*" within a SysML-Namespace /
   * resolve through SysMLsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testSysMLScopeStarImport() throws IOException {
    var parentModel = "package Other { part def Parent; }";
    var model = "package test { private import Other::*; part def Child : Parent; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Disabled as it bases on resolve relative names in related scopes
   */
  @Disabled
  @Test()
  public void testRelativeImport() throws IOException {
    var model = ""
        + "package Estranged { "
        + "  part def Uncle { "
        + "    part def Niece { "
        + "      part def Daughter; "
        + "    }"
        + "} "
        + "  part def Father {"
        + "    private import Uncle::Niece;"
        + "    part def NieceClone :> Niece::Daughter;"
        + "  }"
        + "}";

    var ast = processAst(model);

    var partDef = ((ASTPartDef)((ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  )).getSysMLElement(1));
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);
    assertThat(optParent).isNotPresent();

    var fullNameParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentRef.printType());
    assertThat(fullNameParent).isPresent();
    assertThat(fullNameParent.get().getFullName()).isEqualTo("Estranged.Uncle.Niece.Daughter");
  }

  @Test
  public void testWithPublic() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { public import Other::InnerOther::*; part def Child; }";
    var modelImportingAPublic = "package Estranged { private import test::*; part def Uncle : Parent;}";

    processAst(parentModel);
    var ast = processAst(model);
    var astImportingAPublic = processAst(modelImportingAPublic);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) astImportingAPublic.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

  @Test
  public void testWithPublicRecursive() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { public import Other::**; part def Child; }";
    var modelImportingAPublic = "package Estranged { private import test::*; part def Uncle : Parent;}";

    processAst(parentModel);
    var ast = processAst(model);
    var astImportingAPublic = processAst(modelImportingAPublic);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) astImportingAPublic.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

  @Test
  public void testRecursiveWithPublic() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { public import Other::InnerOther::*; part def Child; }";
    var modelImportingAPublic = "package Estranged { private import test::**; part def Uncle : Parent;}";

    processAst(parentModel);
    processAst(model);
    var astImportingAPublic = processAst(modelImportingAPublic);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) astImportingAPublic.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

  /**
   * Here we test if resolution from specializations in the same scope as the imports
   * are found. Another use case for these are constraints with imports and expression
   * referencing imports on the same level.
   */
  @Test
  public void testSameScopeSpecialization() throws IOException {
    var parentModel = "package Other { package InnerOther { requirement refReq; } }";
    var model = "package test { private import Other::InnerOther::*; satisfy refReq; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTRequirementUsage) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveRequirementUsage(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.refReq");
  }

  /**
   * Here we test if imports in body are not resolved in specializations
   */
  @Disabled
  @Test
  public void testInvalidSameScopeSpecialization() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { part def ParentClone :> Parent { private import Other::InnerOther::*; } }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(0  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);
    assertThat(optParent).isNotPresent();
  }

  @Test()
  public void testDoubledNames() throws IOException {
    var parentModel = "package Other { part def Parent { part def Parent; } }";
    var model = "package test { private import Other::*; part def Child : Parent; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  @Test
  public void testDoubledNames2() throws IOException {
    var parentModel = "package Other { part def Parent { part def Parent; } }";
    var model = "package test { private import Other::*::**; part def Child : Parent; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    // this only finds Other.Parent without finding Other.Parent.Parent because its shadowing and foundSymbols is set by first
    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  @Test
  public void testDoubledNamesExclusion() throws IOException {
    var parentModel = "package Other { part def Parent { part def Parent; } }";
    var model = "package test { private import Other::Parent::*::**; part def Child : Parent; }";

    processAst(parentModel);
    var ast = processAst(model);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent.Parent");
  }

  /**
   * TODO add 2 imports that import indirectly the same thing like in ISQ::* direct as a public and SI::* imports
   *   both import ISQ one transitive and one direct
   */
  @Test
  public void testWrongConstellation() throws IOException {
    var parentModel = "package Other { part def Parent { part def Parent; } }";
    var model = "package test { private import Other::Parent; part def Child : Parent;  } part def Parent; ";

    processAst(parentModel);
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    // cannot complete symboltable because Parent resolves to multiple. public imports would not work.
    var as = tool.createSymbolTable(ast);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveTypeMany(parentName);

    assertThat(optParent).size().isEqualTo(2); // check if we did resolve
    assertThat(optParent).map(TypeSymbolTOP::getFullName).contains("Other.Parent", "Parent");
  }

  @Test
  public void testPublicsWithoutAst() throws IOException {
    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { public import Other::InnerOther::*; part def Child; }";
    var modelImportingAPublic = "package Estranged { private import test::**; part def Uncle : Parent;}";

    var parentAst = processAst(parentModel);
    var ast = processAst(model);
    var astImportingAPublic = processAst(modelImportingAPublic);

    SysMLv2GlobalScope globalScope = (SysMLv2GlobalScope) tool.getGlobalScope();
    var serializedParentAst = globalScope.getSymbols2Json().serialize(parentAst.getEnclosingScope());
    var serializedAst = globalScope.getSymbols2Json().serialize(ast.getEnclosingScope());
    globalScope.removeSubScope(parentAst.getEnclosingScope());
    globalScope.removeSubScope(ast.getEnclosingScope());

    globalScope.addSubScope(globalScope.getSymbols2Json().deserialize(serializedParentAst));
    globalScope.addSubScope(globalScope.getSymbols2Json().deserialize(serializedAst));

    var partDef = (ASTPartDef) ((ASTSysMLPackage) astImportingAPublic.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }


}
