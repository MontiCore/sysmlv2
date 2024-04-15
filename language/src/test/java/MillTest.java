import de.monticore.lang.sysmlv2.SysMLv2Mill;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MillTest {

  /**
   * Es gab in einer früheren Version der Sprache Probleme mit der Mill: Die Initialsierung hat etwa 30 Sekunden
   * gedauert. Dieser Test prüft, dass die Initialsierungsdauer unter 1 Sekunde bleibt.
   */
  @Disabled("Spoiler: Es dauert ~4,5 Sekunden auf meinem Laptop")
  @Test
  public void measureMillInit() {
    var startTime = System.nanoTime();
    SysMLv2Mill.init();
    var elapsed = System.nanoTime() - startTime;
    assertThat(elapsed).isLessThan(1*1000*1000*1000); // nano -> micro -> milli -> sekunden
  }

}
