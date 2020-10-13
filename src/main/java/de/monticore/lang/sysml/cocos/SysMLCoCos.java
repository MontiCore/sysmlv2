package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.cocos.naming.DefinitionNameStartsWithCapitalLetter;
import de.monticore.lang.sysml.sysml._cocos.SysMLCoCoChecker;
import jline.internal.Log;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLCoCos {
  public SysMLCoCoChecker getCheckerForAllCoCos() {
    final SysMLCoCoChecker checker = new SysMLCoCoChecker();
    checker.addCoCo(new DefinitionNameStartsWithCapitalLetter());
    //checker.addCoCo(new StateNameStartsWithCapitalLetter()); TODO

    return checker;
  }



  public static String getErrorCode(SysMLCoCoName name) {
    StringBuilder res = new StringBuilder();
    res.append("0xSysML"); //Hexadezmal TODO
    switch (name) {
      case CommonFileExtension:
        return res.append("01").toString();
      case ValidImportStatement:
        return res.append("02").toString();
      case PackageNameEqualsFileName:
        return res.append("03").toString();
      case DefinitionNameStartsWithCapitalLetter:
        return res.append("04").toString();
      case NameReference:
        return res.append("xx").toString();
      case UniqueName:
        return res.append("xx").toString();
      case ImportIsDefined:
        return res.append("xx").toString();
      case ImportWithStarAndWithAs:
        return res.append("xx").toString();
      case PackageImportWithoutStar:
        return res.append("xx").toString();
      case AmbiguousImport:
        return res.append("xx").toString();
      case ImportedElementNameAlreadyExists:
        return res.append("xx").toString();
      default:
        Log.error("Internal error: One CoCo was not registered correctly.");
        return res.append("00").toString();
    }
  }
}
