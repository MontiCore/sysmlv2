package parser;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlconnections._ast.ASTAllocationUsageParam;
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
 * The ends of binary and n-ary connections and allocations: an optional end name with a reference
 * subsetting and exactly one connected feature; the n-ary forms have at least two ends.
 */
public class ConnectionEndsTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    SysMLv2Mill.init();
  }

  ASTConnectionUsage connection(String connection) throws IOException {
    ASTSysMLModel model = SysMLv2Mill.parser().parse_String("part def P { " + connection + " }").orElseThrow();
    assertThat(Log.getErrorCount()).isZero();
    ASTPartDef part = (ASTPartDef) model.getSysMLElement(0);
    return (ASTConnectionUsage) part.getSysMLElement(0);
  }

  List<ASTEndpoint> ends(String connection) throws IOException {
    return connection(connection).getEndpointList();
  }

  static List<String> targets(List<ASTEndpoint> ends) {
    return ends.stream().map(e -> e.getMCQualifiedName().getQName()).toList();
  }

  @Test
  public void namedEndsAreSeparatedByCommas() throws IOException {
    List<ASTEndpoint> ends = ends("connect (s ::> a.p, t ::> b.q, u references c.r);");
    assertThat(ends).hasSize(3);
    assertThat(ends.stream().map(ASTEndpoint::getName).toList()).containsExactly("s", "t", "u");
    assertThat(targets(ends)).containsExactly("a.p", "b.q", "c.r");
  }

  @Test
  public void endsMayBeQualifiedNamesAlone() throws IOException {
    List<ASTEndpoint> ends = ends("connection bus connect (d1, d2.p, d3);");
    assertThat(ends).hasSize(3);
    assertThat(ends.stream().noneMatch(ASTEndpoint::isPresentName)).isTrue();
    assertThat(targets(ends)).containsExactly("d1", "d2.p", "d3");
  }

  @Test
  public void binaryEndsMayBeNamedLikeNaryEnds() throws IOException {
    ASTConnectionUsage connection = connection("connect e1 ::> a.p to e2 ::> b.q;");
    // the reference names the connected feature, the name before it is the end name
    assertThat(connection.getSrc().getName()).isEqualTo("e1");
    assertThat(connection.getSrc().getMCQualifiedName().getQName()).isEqualTo("a.p");
    assertThat(connection.getTgt().getName()).isEqualTo("e2");
    assertThat(connection.getTgt().getMCQualifiedName().getQName()).isEqualTo("b.q");
  }

  @Test
  public void unnamedBinaryEndsStayAsBefore() throws IOException {
    ASTConnectionUsage connection = connection("connect a.p to b.q;");
    assertThat(connection.getSrc().isPresentName()).isFalse();
    assertThat(connection.getSrc().getMCQualifiedName().getQName()).isEqualTo("a.p");
    assertThat(connection.getTgt().getMCQualifiedName().getQName()).isEqualTo("b.q");
  }

  @Test
  public void anNaryConnectionNeedsAtLeastTwoEnds() throws IOException {
    for (String connection : List.of("connect (a.p);", "connect ();")) {
      Log.clearFindings();
      assertThat(SysMLv2Mill.parser().parse_String("part def P { " + connection + " }")).isEmpty();
      assertThat(Log.getErrorCount()).as(connection).isPositive();
    }
  }

  @Test
  public void anNaryAllocationHasAnyNumberOfEnds() throws IOException {
    ASTSysMLModel model = SysMLv2Mill.parser()
        .parse_String("part def P { allocate (logical ::> l, physical ::> p, c.d); }").orElseThrow();
    assertThat(Log.getErrorCount()).isZero();
    var allocation = (ASTAllocationUsageParam) ((ASTPartDef) model.getSysMLElement(0)).getSysMLElement(0);
    assertThat(targets(allocation.getEndpointList())).containsExactly("l", "p", "c.d");
    assertThat(allocation.getEndpoint(0).getName()).isEqualTo("logical");
  }

  @Test
  public void anNaryAllocationNeedsAtLeastTwoEnds() throws IOException {
    assertThat(SysMLv2Mill.parser().parse_String("part def P { allocate (l); }")).isEmpty();
    assertThat(Log.getErrorCount()).isPositive();
  }

  @Test
  public void namedAndUnnamedEndsMix() throws IOException {
    List<ASTEndpoint> ends = ends("connect (s ::> a.p, own, t ::> b.q);");
    assertThat(targets(ends)).containsExactly("a.p", "own", "b.q");
  }
}
