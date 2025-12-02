package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTSendActionUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.types3.TypeCheck3;

public class SendActionTypeCheck3 implements SysMLActionsASTSendActionUsageCoCo {

  @Override
  public void check(ASTSendActionUsage node) {
    // Wir gehen davon aus, dass Send-Actions die Kanäle auf Ports nicht als Strom,
    // sondern Element-Weise (i.e. Event-basiert) verarbeiten

    var payloadType = TypeCheck3.typeOf(node.getPayload());
    if(payloadType.isObscureType()) {
      // Error should already be logged?
    }
    // Vergleich zum Target steht noch aus.
    // TODO Target zur Expression erheben (Grammatik ändenr), Checken, dann vergleichen
  }
}
