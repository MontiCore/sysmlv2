package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConnectedVariableExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.QualifiedPortNameExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.SubPartNamesInConnectionExistCoCo;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritedConnectionResolutionTest {

  private static final SysMLv2Tool tool = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addCollectionTypes();
    Log.clearFindings();
  }

  @Disabled("resolvePartUsageLocallyMany not overridden yet")
  @Test
  public void testAA3InheritedSubPartUsage() throws IOException {
    String model =
        "part def A { "
      + "port i: boolean; "
      + "} "

      + "part def B { "
      + "part a: A; "
      + "} "

      + "part def C specializes B { "
      + "port o: boolean; "
      + "connect a.i to o; "
      + "}";

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
  public void testAA4InheritedQualifiedPort() throws IOException {
    String model =
        "part def A { "
      + "port i: boolean; "
      + "} "

      + "part def B specializes A { "
      + "} "

      + "part def C { "
      + "part b: B; "
      + "port o: boolean; "
      + "connect b.i to o; "
      + "}";

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
  public void testAD0InheritedUnqualifiedSourcePort() throws IOException {
    String model =
        "part def A { "
      + "port i: boolean; "
      + "} "

      + "part def B specializes A { "
      + "port o: boolean; "
      + "connect i to o; "
      + "}";

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
  public void testAD1InheritedUnqualifiedTargetPort() throws IOException {
    String model =
        "part def A { "
      + "port i: boolean; "
      + "} "

      + "part def B specializes A { "
      + "port o: boolean; "
      + "connect o to i; "
      + "}";

    var ast = SysMLv2Mill.parser().parse_String(model).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConnectedVariableExistsCoCo());
    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @AfterEach
  void clearLog() {
    Log.clearFindings();
    Log.enableFailQuick(true);
  }
}
