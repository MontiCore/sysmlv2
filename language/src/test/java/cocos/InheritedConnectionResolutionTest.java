package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConnectedVariableExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.QualifiedPortNameExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.SubPartNamesInConnectionExistCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritedConnectionResolutionTest {

  @Disabled("resolvePartUsageLocallyMany not overridden yet")
  @Test
  public void testAA3InheritedSubPartUsage() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    String model = "" +
        "part def A { port i; }" +
        "part def B { part a: A; }" +
        "part def C specializes B { connect a.i to a.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new SubPartNamesInConnectionExistCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testAA4InheritedQualifiedPortUsage() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    String model = "" +
        "part def A { port i; }" +
        "part def B specializes A {}" +
        "part def C { part b: B; connect b.i to b.i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new QualifiedPortNameExistsCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testAD0InheritedSourcePortUsage() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    String model = "" +
        "part def A { port i: boolean; }" +
        "part def B specializes A { port o: boolean; connect i to o; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedVariableExistsCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testAD1InheritedTargetPortUsage() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    String model = "" +
        "part def A { port i: boolean; }" +
        "part def B specializes A { port o: boolean; connect o to i; }";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedVariableExistsCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }
}
