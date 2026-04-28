package astrules;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;

import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolBuilder;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.*;

import java.io.IOException;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diese Testklasse validiert die Resolve-Funktion für Feldausdrücke (wie 'a.b') in SysMLv2-Code.
 *
 * testResolveOfVariable testet die direkte Auflösung von Feldausdrücken wie 'a.b' durch Parsing eines
 * kompletten SysMLv2-Modells. Das Ziel ist, einen Feldausdruck zu resolven, ohne vorher manuell die Variable
 * zu resolven und dann in deren Typ nach dem Feld zu suchen. Weitere Tests mit komplexeren Verschachtelungen
 * werden später hinzugefügt.
 */
public class ASTResolveVariablesTest {

  @Disabled("Resolve von a.b schlägt noch fehl")
  @Test
  public void testResolveOfVariable() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model = "" +
        "package Test {\n" +
        "  attribute def A { attribute b: boolean; }\n" +
        "  attribute a: A;\n" +
        "  constraint { a.b }\n" +
        "}";
    var ast = SysMLv2Mill.parser().parse_String(model).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var testPackage = (ASTSysMLPackage) ast.getSysMLElement(0);
    var constr = (ASTConstraintUsage) testPackage.getSysMLElement(2);
    var expr = constr.getExpression();

    var variable = ((ISysMLv2Scope)expr.getEnclosingScope()).resolveField("a.b");
    assertThat(variable).isPresent();
  }

  @Disabled("Resolve von a.b schlägt noch fehl")
  @Test
  public void testResolveFieldWithoutAdapters() {
    LogStub.init();
    SysMLv2Mill.init();
    ISysMLv2ArtifactScope artifactScope = SysMLv2Mill.artifactScope();
    SysMLv2Mill.prepareGlobalScope();

    var booleanSymbol = SysMLv2Mill.globalScope().resolveType("boolean").get();
    artifactScope.add(booleanSymbol);

    var boolType = SymTypeExpressionFactory.createPrimitive(booleanSymbol);
    var fieldSymbol = new FieldSymbolBuilder().setName("b").setType(boolType).build();
    artifactScope.add(fieldSymbol);
    var typeAScope = SysMLv2Mill.scope();
    typeAScope.add(fieldSymbol);
    var typeSymbolA = new TypeSymbolBuilder().setName("A").setSpannedScope(typeAScope).build();
    artifactScope.add(typeSymbolA);

    var typeA = SymTypeExpressionFactory.createTypeObject(typeSymbolA);
    var variableSymbol = new VariableSymbolBuilder().setName("a").setType(typeA).build();
    artifactScope.add(variableSymbol);

    var field = artifactScope.resolveField("a.b");
    assertThat(field).isPresent();

  }
}
