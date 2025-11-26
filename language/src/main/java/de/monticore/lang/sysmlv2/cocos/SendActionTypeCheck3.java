package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTSendActionUsage;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTSendActionUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;

public class SendActionTypeCheck implements SysMLActionsASTSendActionUsageCoCo {

  @Override
  public void check(ASTSendActionUsage node) {
    // Wir gehen davon aus, dass Send-Actions die Kanäle auf Ports nicht als Strom,
    // sondern Element-Weise (i.e. Event-basiert) verarbeiten
    var deriver = new SysMLDeriver(false);
    deriver.init();

    var payloadType = deriver.deriveType(node.getPayload());
    if(!payloadType.isPresentResult() || payloadType.getResult().isObscureType()) {
      // Error should already be logged?
    }
    // Vergleich zum Target steht noch aus.
    // TODO Target zur Expression erheben (Grammatik ändenr), Checken, dann vergleichen
  }

}
