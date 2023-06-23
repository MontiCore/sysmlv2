package symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComponentSymbolTest {

  @Test
  public void test() throws IOException {
    var tool = new SysMLv2Tool();
    tool.init();

    var model = "part def A;";
    var ast = SysMLv2Mill.parser().parse_String(model).get();

    var as = tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var st = new SysMLv2Symbols2Json().serialize(as);

    assertThat(st).isEqualTo("{\"generated-using\":\"www.MontiCore.de technology\",\"name\":\"A\",\"symbols\":"
        + "[{\"kind\":\"de.monticore.lang.sysmlparts._symboltable.PartDefSymbol\",\"name\":\"A\","
        + "\"requirementType\":\"UNKNOWN\"}]}");

    var comp = as.resolveComponent("A");
    assertThat(comp).isPresent();
  }

}
