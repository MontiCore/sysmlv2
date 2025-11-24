package de.monticore.lang.componentconnector;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;

public class StreamTimingUtil {
  public static String mapTimingToStreamType(Timing timing) {
    switch (timing) {
      case TIMED:
        return "EventStream";
      case TIMED_SYNC:
        return "SyncStream";
      case UNTIMED:
        return "UntimedStream";
      default:
        throw new IllegalArgumentException("Unexpected value: " + timing);
    }
  }
}
