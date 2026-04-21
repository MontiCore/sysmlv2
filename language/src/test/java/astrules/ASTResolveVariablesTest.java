package astrules;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlbasis._symboltable.ISysMLBasisScope;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;

import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diese Testklasse validiert die Resolve-Funktion für Variablenreferenzen in SysMLv2-Code.
 *
 * testCreateSymFiles erstellt symboltable-Dateien, um damit die Resolve-Funktion
 * zu testen ohne dass sie Zugriff auf den AST benötigt.
 *
 * testResolveOfVariables testet die Auflösung von Variablen wie 'a' und 'a.b' in einem Beispielmodell.
 *
 */
public class ASTResolveVariablesTest {

  //@Disabled("Resolve von a.b schlägt noch fehl")
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

    var variable = ((ISysMLv2Scope)expr.getEnclosingScope()).resolveVariable("a.b");
    assertThat(variable).isPresent();
  }
}
