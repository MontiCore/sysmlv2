package symboltable;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2DeSer;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.symboltable.completers.ImportsCompleter;
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

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

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
  @Test()
  public void testSysMLScopeRecursiveImport() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    // working constellation
    //var parentModel = "package Other { package InnerOther { part def Parent; } }";
    //var model = "package test { public import Other::InnerOther::**; part def Child; }";
    //var modelImportingAPublic = "package Estranged { private import test::*; part def Uncle : Parent;}";
    // the simple import check happens if you have multiple name parts of the "name"? Dont think so
    // what do you do if you find the symbol itself? does it go inside itself? Actually it should bcuz in the sysml it is allowed

    // constellation works.
    //var parentModel = "package Other { package InnerOther { part def Parent; } }";
    //var model = "package test { public import Other::**; part def Child; }";
    //var modelImportingAPublic = "package Estranged { private import test::*; part def Uncle : Parent;}";

    var parentModel = "package Other { package InnerOther { part def Parent; } }";
    var model = "package test { public import Other::**; part def Child; }";
    // you cant import here Sibling::* where Sibling is defined
    var modelImportingAPublic = ""
        + "package Estranged { "
        + "  part def Sibling { "
        + "    part def Niece { "
        + "      part def Daughter; "
        + "    }"
        + "} "
        + "  part def Uncle {"
        + "    private import Sibling::Niece;"
        + "    part def NieceClone :> Niece::Daughter;"
        + "  }"
        + "}";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel).get();
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    var astImportingAPublic = SysMLv2Mill.parser().parse_String(modelImportingAPublic).get();

    tool.createSymbolTable(parentAst);
    tool.completeSymbolTable(parentAst);
    tool.finalizeSymbolTable(parentAst);

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    tool.createSymbolTable(astImportingAPublic);
    tool.completeSymbolTable(astImportingAPublic);
    tool.finalizeSymbolTable(astImportingAPublic);

    var partDef = (ASTPartDef) ((ASTSysMLPackage) astImportingAPublic.getSysMLElement(0)).getSysMLElement(1  );
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    var parentName = parentRef.getNameList().get(0);

    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent(); // check if we did resolve
    assertThat(optParent.get().getFullName()).isEqualTo("Other.InnerOther.Parent");

    //var res = ((SysMLv2GlobalScope)SysMLv2Mill.globalScope()).getSymbols2Json().serialize(ast.getEnclosingScope());
    //var resBack = ((SysMLv2GlobalScope)SysMLv2Mill.globalScope()).getSymbols2Json().deserialize(res);
    //assertThat(res).isNotEmpty();
  }

  @Test
  public void testSpecialImports() throws IOException {
    var as3 = process(""
        + "package WithNesting {"
        + "  part def B {"
        + "    part b {"
        + "      port c;"
        + "    }"
        + "  }"
        + "  "
        + "  part wrong :> WithNesting::B.b;"
        + "  part resolves :> WithNesting::B::b;"
        + "  part wrong1 :> b;"
        + "  part resolves1 :> B::b;"
        + "  port resolves2 :> B::b.c;"
        + "  port resolves3 :> B::b::c;"
        + ""
        + "  part a: B {"
        + "    part g {"
        + "      port h;"
        + "    }"
        + "  }"
        + ""
        + "  part e :> a.b;"
        + "  port f :> a.b.c;"
        + "  port j :> a.g.h;"
        + "  part i :> 'Wi:th\\Special Chars '::k;"
        + "  part o :> 'Wi:th\\Special Chars '::k.b;"
        + ""
        + "  part def E :> B;"
        + "  part resolves4 :> E::b;"
        + "  part m : E;"
        + "  part n :> m.b;"
        + "}");

    var as4 = process(""
        + "package ForImports {"
        + "  private import WithNesting::**;"
        + "}");

    var package4 = as4.getSubScopes().get(0);
    SysMLv2Traverser tra = SysMLv2Mill.inheritanceTraverser();
    tra.add4SysMLImportsAndPackages(new ImportsCompleter());
    package4.getAstNode().accept(tra);

    // what about fully qn with the same name but different symbols that span the scope.

    // resolving from an unnamed scope with/without imports
  }

}
