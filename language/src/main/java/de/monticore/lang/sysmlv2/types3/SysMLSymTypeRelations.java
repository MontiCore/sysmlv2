package de.monticore.lang.sysmlv2.types3;

import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.BuiltInTypeRelations;
import de.monticore.types3.util.SymTypeRelationsDefaultDelegatee;

public abstract class SysMLSymTypeRelations extends OCLSymTypeRelations {

  public static void init() {
    setDelegate(new SysMLSymTypeRelationsDelegatee());
  }

  // selecting the concrete implementations
  protected static class SysMLSymTypeRelationsDelegatee extends
      SymTypeRelationsDefaultDelegatee {
    public SysMLSymTypeRelationsDelegatee() {
      builtInRelationsDelegate = new BuiltInTypeRelations() {
        @Override
        public boolean isIntegralType(SymTypeExpression type) {
          return super.isIntegralType(type) ||
              type.isPrimitive() &&
                  type.asPrimitive().getPrimitiveName().equals("nat");
        }
      };
    }
  }
}
