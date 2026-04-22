package symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportResolveTest {

  /**
   * Tests the general resolution of FQN without using imports.
   * That is the main downstream feature the import-tests rely on, because
   * while processing the resolve by imports we do try to resolve their FQNs.
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Test()
  public void testFQNResolving() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { part def Parent; }";
    var model = "part def Child : Other.Parent;";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { part def Parent; }";
    var model = "private import Other::Parent; part def Child : Parent;";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { part def Parent; }";
    var model = "private import Other::*; part def Child : Parent;";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
  @Disabled("Recursive Imports are currently unsupported in our implementation")
  @Test()
  public void testArtifactsScopeRecursiveImport() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "private import Other::**; part def Child : Parent;";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var partDef = (ASTPartDef) ast.getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolvePartDef(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

  /**
   * Tests direct SysML Import "Other::Parent" within a SysML-Namespace /
   * resolve through SysMLsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  // @Disabled("WIP")
  @Test()
  public void testSysMLScopeImport() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { part def Parent; }";
    var model = "package test { private import Other::Parent; part def Child : Parent; }";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

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
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { part def Parent; }";
    var model = "package test { private import Other::*; part def Child : Parent; }";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.Parent");
  }

  /**
   * Recursive Imports are currently not handled by our implementation.
   * Tests star/wildcard SysML Import "Other::**" within a SysML-Namespace /
   * resolve through SysMLsScope
   *
   * @throws IOException Mills parser exception, shall not happen
   */
  @Disabled("Recursive Imports are currently unsupported in our implementation")
  @Test()
  public void testSysMLScopeRecursiveImport() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { private import Other::**; part def Child : Parent; }";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) ast.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolvePartDef(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");
  }

}
