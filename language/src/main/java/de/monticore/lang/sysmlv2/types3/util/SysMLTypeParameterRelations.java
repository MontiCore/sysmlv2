package de.monticore.lang.sysmlv2.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.util.TypeParameterRelationsDefaultDelegatee;

public class SysMLTypeParameterRelations extends TypeParameterRelationsDefaultDelegatee {

  public static void init() {
    setDelegate(new SysMLTypeParameterRelations());
  }

  @Override
  protected <T extends SymTypeExpression> T _getCaptureConverted(T type) {
    // Ordinary predicate types need no capture conversion. The default
    // capturer expects a generic function symbol and reports internal errors.
    if (type.isFunctionType() && !hasWildcards(type) && !hasInferenceVariables(type)) {
      return (T) type.deepClone();
    }
    return super._getCaptureConverted(type);
  }
}
