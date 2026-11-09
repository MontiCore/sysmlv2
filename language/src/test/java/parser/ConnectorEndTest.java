package parser;

import de.monticore.lang.sysmlbasis._ast.ASTConnectorEnd;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The ends of an n-ary connection are separated by commas. With a SysMLParameter as end, the
 * comma was taken as a further supertype of a reference subsetting, and
 * {@code connect (s ::> a.p, t ::> b.q)} became one end {@code s ::> [a.p, t] ::> [b.q]}.
 */
public class ConnectorEndTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    SysMLv2Mill.init();
  }

  List<ASTConnectorEnd> ends(String connection) throws IOException {
    ASTSysMLModel model = SysMLv2Mill.parser().parse_String("part def P { " + connection + " }").orElseThrow();
    assertThat(Log.getErrorCount()).isZero();
    ASTPartDef part = (ASTPartDef) model.getSysMLElement(0);
    return ((ASTConnectionUsage) part.getSysMLElement(0)).getConnectorEndList();
  }

  static List<String> targets(List<ASTConnectorEnd> ends) {
    return ends.stream().map(e -> e.getMCQualifiedName().getQName()).toList();
  }

  @Test
  public void namedEndsAreSeparatedByCommas() throws IOException {
    List<ASTConnectorEnd> ends = ends("connect (s ::> a.p, t ::> b.q, u references c.r);");
    assertThat(ends).hasSize(3);
    assertThat(ends.stream().map(ASTConnectorEnd::getName).toList()).containsExactly("s", "t", "u");
    assertThat(targets(ends)).containsExactly("a.p", "b.q", "c.r");
  }

  @Test
  public void endsMayBeQualifiedNamesAlone() throws IOException {
    List<ASTConnectorEnd> ends = ends("connection bus connect (d1, d2.p, d3);");
    assertThat(ends).hasSize(3);
    assertThat(ends.stream().noneMatch(ASTConnectorEnd::isPresentName)).isTrue();
    assertThat(targets(ends)).containsExactly("d1", "d2.p", "d3");
  }

  @Test
  public void namedAndUnnamedEndsMix() throws IOException {
    List<ASTConnectorEnd> ends = ends("connect (s ::> a.p, own, t ::> b.q);");
    assertThat(targets(ends)).containsExactly("a.p", "own", "b.q");
  }
}
