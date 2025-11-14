package de.monticore.lang.componentconnector;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;

public class StreamTimingUtil {
  public static String mapTimingToStreamType(Timing timing) {
    return switch (timing) {
      case TIMED -> "EventStream";

      case TIMED_SYNC -> "SyncStream";

      case UNTIMED -> "UntimedStream";
    };
  }
}
