package de.monticore.lang.sysmlv2.cocos;


import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.se_rwth.commons.logging.Log;


public class PortsGeneratorCoCos implements SysMLPartsASTPortUsageCoCo {

  /**
   * Check that at least one part def is extended.
   */
  @Override public void check(ASTPortUsage node) {

    if(!node.isPresentSysMLFeatureDirection()){
      Log.error("The Port usage "+ node.getName()+" has no direction but needs one.");
    }

  }



}
