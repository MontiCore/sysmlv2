package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLCoCos {
  public SysMLCoCoChecker getCheckerForAllCoCos() {
    final SysMLCoCoChecker checker = new SysMLCoCoChecker();
    //checker.addCoCo(new AtLeastOneInitialAndFinalState());
    //checker.addCoCo(new StateNameStartsWithCapitalLetter());

    return checker;
  }
}
