package de.monticore.lang.sysmlv2.types3.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SysMLSymTypeBoxingVisitor extends de.monticore.types3.util.SymTypeBoxingVisitor {

  protected static final Map<String, String> sysMLPrimitiveBoxMap;
  protected static final Map<String, String> sysMLObjectBoxMap;
  protected static final Map<String, String> sysMLGenericBoxMap;

  static {
    Map<String, String> primitiveBoxMap_temp = new HashMap<>();
    primitiveBoxMap_temp.put("boolean", "ScalarValues.Boolean");
    primitiveBoxMap_temp.put("byte", "ScalarValues.Integer");
    primitiveBoxMap_temp.put("char", "ScalarValues.Natural");
    primitiveBoxMap_temp.put("double", "ScalarValues.Rational");
    primitiveBoxMap_temp.put("float", "ScalarValues.Rational");
    primitiveBoxMap_temp.put("int", "ScalarValues.Integer");
    primitiveBoxMap_temp.put("long", "ScalarValues.Integer");
    primitiveBoxMap_temp.put("short", "ScalarValues.Integer");
    primitiveBoxMap_temp.put("nat", "ScalarValues.Natural");
    sysMLPrimitiveBoxMap = Collections.unmodifiableMap(primitiveBoxMap_temp);

    Map<String, String> objectBoxMap_temp = new HashMap<>();
    objectBoxMap_temp.put("String", "ScalarValues.String");
    sysMLObjectBoxMap = Collections.unmodifiableMap(objectBoxMap_temp);

    /* Können Optional nicht boxen.
     * Stattdessen Optional<T> mit T[0..1] darstellen?
     */
    Map<String, String> genericBoxMap_temp = new HashMap<>();
    genericBoxMap_temp.put("Set", "Collections.Set");
    genericBoxMap_temp.put("List", "Collections.List");
    genericBoxMap_temp.put("Map", "Collections.Map");
    sysMLGenericBoxMap = Collections.unmodifiableMap(genericBoxMap_temp);
  }

  @Override
  public Map<String, String> getPrimitiveBoxMap() {
    return sysMLPrimitiveBoxMap;
  }

  @Override
  public Map<String, String> getObjectBoxMap() {
    return sysMLObjectBoxMap;
  }

  @Override
  public Map<String, String> getGenericBoxMap() {
    return sysMLGenericBoxMap;
  }
}
