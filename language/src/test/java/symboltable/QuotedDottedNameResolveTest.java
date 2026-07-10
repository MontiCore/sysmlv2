/* (c) https://github.com/MontiCore/monticore */
package symboltable;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

public class QuotedDottedNameResolveTest {

  /**
   * Resolves an unqualified quoted dotted type name through a package-level
   * star import.
   *
   * Package B imports A::*, and the PartUsage C is typed by 'name.with.dots'.
   * The quoted name must stay one segment and resolve to the imported
   * PartDefinition A.'name.with.dots'.
   */
  @Disabled("Currently can't resolve quoted dotted type name")
  @Test
  public void testPackageStarImportResolvesQuotedDottedName() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var parentModel = "package A {"
        + "part def 'name.with.dots';"
        + "}";
    var model = "package B {"
        + "private import A::*; "
        + "part C : 'name.with.dots';"
        + "}";

    var parentAst = SysMLv2Mill.parser().parse_String(parentModel);
    var ast = SysMLv2Mill.parser().parse_String(model);

    tool.createSymbolTable(parentAst.get());
    tool.completeSymbolTable(parentAst.get());
    tool.finalizeSymbolTable(parentAst.get());

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    var packageB =  (ASTSysMLPackage) ast.get().getSysMLElement(0);
    var partUsage = (ASTPartUsage) packageB.getSysMLElement(1);

    var parentRef = (ASTMCQualifiedType) partUsage.getSpecialization(0).getSuperTypes(0);

    assertThat(parentRef.getNameList()).containsExactly("name.with.dots");

    var parentName = parentRef.getNameList().get(0);
    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent();
    assertThat(optParent.get().getName()).isEqualTo("name.with.dots");
  }

  /**
   * Resolves a local quoted type name that contains dots.
   *
   * The quoted reference 'name.with.dots' must be represented as one AST segment:
   *
   *   name.with.dots
   *
   * and must resolve to the local PartDefinition 'name.with.dots'. The dots
   * inside the quoted name must not be interpreted as qualified-name separators.
   */
  @Disabled("Currently can't resolve quoted dotted type name")
  @Test
  public void testResolveLocalQuotedDottedName() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model =
        "part def 'name.with.dots'; " +
            "part def B : 'name.with.dots';";

    var ast = SysMLv2Mill.parser().parse_String(model);

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    var partDef = (ASTPartDef) ast.get().getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    assertThat(parentRef.getNameList()).containsExactly("name.with.dots");

    var parentName = parentRef.getNameList().get(0);
    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent();
    assertThat(optParent.get().getName()).isEqualTo("name.with.dots");
  }

  /**
   * Resolves a fully qualified type name whose final segment is a quoted name
   * containing dots.
   *
   * The reference a.b.'name.with.dots' must be represented as:
   *
   * a / b / name.with.dots
   *
   * and must resolve to the PartDefinition 'name.with.dots' inside package a.b.
   */
  @Disabled("Currently can't resolve quoted dotted type name")
  @Test
  public void testResolveFullyQualifiedQuotedDottedName() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model =
        "package a { " +
            "  package b { " +
            "    part def 'name.with.dots'; " +
            "  } " +
            "} " +
            "part def B : a.b.'name.with.dots';";

    var ast = SysMLv2Mill.parser().parse_String(model);

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    var partDef = (ASTPartDef) ast.get().getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    assertThat(parentRef.getNameList()).containsExactly("a", "b", "name.with.dots");

    var parentName = String.join(".", parentRef.getNameList());
    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent();
    assertThat(optParent.get().getName()).isEqualTo("name.with.dots");
  }

  /**
   * Distinguishes a normal qualified type name from a quoted simple type name
   * that contains dots.
   *
   *   B1 : name.with.dots
   *     -> name / with / dots
   *
   *   B2 : 'name.with.dots'
   *     -> name.with.dots
   *
   * This test ensures that quoted dotted names are not treated as normal
   * qualified names, and that fixing quoted names does not break ordinary
   * qualified-name resolution.
   */
  @Disabled("Currently can't resolve quoted dotted type name")
  @Test
  public void testDistinguishQuotedDottedNameFromQualifiedName() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model =
        "package name { " +
            "  package with { " +
            "    part def dots; " +
            "  } " +
            "} " +
            "part def 'name.with.dots'; " +
            "part def B1 : name.with.dots; " +
            "part def B2 : 'name.with.dots';";

    var ast = SysMLv2Mill.parser().parse_String(model);

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    var b1 = (ASTPartDef) ast.get().getSysMLElement(2);
    var b1Ref = (ASTMCQualifiedType) b1.getSpecialization(0).getSuperTypes(0);

    assertThat(b1Ref.getNameList()).containsExactly("name", "with", "dots");

    var b2 = (ASTPartDef) ast.get().getSysMLElement(3);
    var b2Ref = (ASTMCQualifiedType) b2.getSpecialization(0).getSuperTypes(0);

    assertThat(b2Ref.getNameList()).containsExactly("name.with.dots");

    var b1Name = String.join(".", b1Ref.getNameList());
    var optB1Parent = ((ISysMLv2Scope) b1Ref.getEnclosingScope()).resolveType(b1Name);

    assertThat(optB1Parent).isPresent();
    assertThat(optB1Parent.get().getName()).isEqualTo("dots");

    var b2Name = b2Ref.getNameList().get(0);
    var optB2Parent = ((ISysMLv2Scope) b2Ref.getEnclosingScope()).resolveType(b2Name);

    assertThat(optB2Parent).isPresent();
    assertThat(optB2Parent.get().getName()).isEqualTo("name.with.dots");
  }

  /**
   * Resolves a qualified type name whose package qualifier is a quoted name
   * containing dots.
   *
   * The reference a.'b.with.dots'.C must be represented as:
   *
   * a / b.with.dots / C
   *
   * and must resolve to the PartDefinition C inside package a.'b.with.dots'.
   */
  @Test
  public void testResolveFQNWithQuotedDottedPackageName() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model =
        "package a { " +
            "  package 'b.with.dots' { " +
            "    part def C; " +
            "  } " +
            "} " +
            "part def B : a.'b.with.dots'.C;";

    var ast = SysMLv2Mill.parser().parse_String(model);

    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    var partDef = (ASTPartDef) ast.get().getSysMLElement(1);
    var parentRef = (ASTMCQualifiedType) partDef.getSpecialization(0).getSuperTypes(0);

    assertThat(parentRef.getNameList()).containsExactly("a", "b.with.dots", "C");

    var parentName = String.join(".", parentRef.getNameList());
    var optParent = ((ISysMLv2Scope) parentRef.getEnclosingScope()).resolveType(parentName);

    assertThat(optParent).isPresent();
    assertThat(optParent.get().getName()).isEqualTo("C");
  }
}
