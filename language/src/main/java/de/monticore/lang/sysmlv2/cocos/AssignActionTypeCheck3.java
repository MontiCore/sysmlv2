package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTAssignmentActionUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTAssignmentActionUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.types.check.TypeCheck;
import de.monticore.types3.TypeCheck3;

public class AssignActionTypeCheck3 implements SysMLActionsASTAssignmentActionUsageCoCo {

  @Override
  public void check(ASTAssignmentActionUsage node) {
    // Wir gehen davon aus, dass Send-Actions die Kanäle auf Ports nicht als Strom,
    // sondern Element-Weise (i.e. Event-basiert) verarbeiten

    var type = TypeCheck3.typeOf(node.getValueExpression());
    if(!type.isObscureType()) {
      // Error should already be logged?
    }
    // Vergleich zum Target steht noch aus.
    // TODO Target zur Expression erheben (Grammatik ändenr), Checken, dann vergleichen
  }

}
