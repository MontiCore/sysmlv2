package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.sysmlv2.types3.util.SysMLSymTypeBoxingVisitor;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types3.util.SymTypeRelationsDefaultDelegatee;

public abstract class SysMLSymTypeRelations extends OCLSymTypeRelations {

  public static void init() {
    setDelegate(new SysMLSymTypeRelationsDelegatee());
  }

  // selecting the concrete implementations
  protected static class SysMLSymTypeRelationsDelegatee extends
      SymTypeRelationsDefaultDelegatee {
    public SysMLSymTypeRelationsDelegatee() {
      builtInRelationsDelegate = new SysMLBuiltInTypeRelations();
      boxingVisitor = new SysMLSymTypeBoxingVisitor();
    }
  }
}
