package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ForAllExpressionCoCos;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ForAllExpressionCoCosTest {
  @Test
  public void testInvalidTerms() throws IOException {
    // Nicht auf StdOut loggen
    LogStub.init();

    var model = "constraint { forall i in {1,2,3}: i>0 }";

    // Initialisierung wie bei echtem Start des Tools
    var tool = new SysMLv2Tool();
    tool.init();

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    // ST wie bei echtem Tool
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ForAllExpressionCoCos());
    checker.checkAll(ast);

    assertThat(Log.getFindings())
        .filteredOn(f -> f.getMsg().contains("0x10090 "))
        .hasSize(1);
  }

  @Test
  public void testInvalidTypes() throws IOException {
    // Nicht auf StdOut loggen
    LogStub.init();

    var model = "constraint { forall int i : i*i > 0 }";

    // Initialisierung wie bei echtem Start des Tools
    var tool = new SysMLv2Tool();
    tool.init();

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    // ST wie bei echtem Tool
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ForAllExpressionCoCos());
    checker.checkAll(ast);

    assertThat(Log.getFindings())
        .filteredOn(f -> f.getMsg().contains("0x10091"))
        .hasSize(1);
  }

  @Test
  public void testValid() throws IOException {
    // Nicht auf StdOut loggen
    LogStub.init();

    var model = "constraint { (1,2,3)->forAll {in i; i > 0} }";

    // Initialisierung wie bei echtem Start des Tools
    var tool = new SysMLv2Tool();
    tool.init();

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    // ST wie bei echtem Tool
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ForAllExpressionCoCos());
    checker.checkAll(ast);

    assertThat(Log.getFindings())
        .hasSize(0);
  }
}
