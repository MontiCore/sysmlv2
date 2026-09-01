package de.monticore.lang.sysmlv2.types3.util;

import java.util.HashMap;
import java.util.Map;

public class SysMLSymTypeBoxingVisitor extends de.monticore.types3.util.SymTypeBoxingVisitor {

  protected static final Map<String, String> sysMLPrimitiveBoxMap;
  protected static final Map<String, String> sysMLObjectBoxMap;

  static {
    Map<String, String> primitiveBoxMap_temp = new HashMap<>(
      de.monticore.types3.util.SymTypeBoxingVisitor.primitiveBoxMap
    );
    primitiveBoxMap_temp.put("nat", "ScalarValues.Natural");
    sysMLPrimitiveBoxMap = Map.copyOf(primitiveBoxMap_temp);

    Map<String, String> objectBoxMap_temp = new HashMap<>(
      de.monticore.types3.util.SymTypeBoxingVisitor.objectBoxMap
    );
    objectBoxMap_temp.put("ScalarValues.String", "java.lang.String");
    sysMLObjectBoxMap = Map.copyOf(objectBoxMap_temp);
  }

  @Override
  public Map<String, String> getPrimitiveBoxMap() {
    return sysMLPrimitiveBoxMap;
  }

  @Override
  public Map<String, String> getObjectBoxMap() {
    return sysMLObjectBoxMap;
  }
}
