package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ImportTest {

  @Test
  public void testImport() throws IOException {
    var tool = new SysMLv2Tool();
    tool.init();
    LogStub.init();
    var parser = SysMLv2Mill.parser();
    var ast = parser.parse_String("package a { package b { part c; } }"
        + "package d { import a.b.c; part e refines c; }");
    tool.createSymbolTable(ast.get());
    tool.completeSymbolTable(ast.get());
    tool.finalizeSymbolTable(ast.get());

    // Kann ich von e aus das c finden?
    var e = ast.get().getEnclosingScope().resolvePartUsage("d.e");
    assertThat(e).isPresent();
    var c = e.get().getEnclosingScope().resolvePartUsage("c");
    assertThat(c).isPresent();
  }
}
