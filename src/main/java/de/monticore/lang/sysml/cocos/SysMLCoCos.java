package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTImportUnitStdCoCo;
import de.monticore.lang.sysml.cocos.imports.ImportStatementValid;
import de.monticore.lang.sysml.cocos.naming.DefinitionNameStartsWithCapitalLetter;
import de.monticore.lang.sysml.cocos.naming.PackageNameEqualsFileName;
import de.monticore.lang.sysml.cocos.naming.UniqueName;
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
    checker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) new ImportStatementValid());
    checker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) new ImportStatementValid());
    checker.addCoCo(new UniqueName());
    checker.addCoCo(new PackageNameEqualsFileName());
    // checker.addCoCo(new NameReference()); Currently we cannot resolve all Name references, so it is no use to test
    // it.


    return checker;
  }



  public static String getErrorCode(SysMLCoCoName name) {
    StringBuilder res = new StringBuilder();
    res.append("0xA71"); //Errorcode are from 0xA7150 - 0xA7200
    switch (name) {
      case CommonFileExtension:
        return res.append("50").toString();
      case ValidImportStatement:
        return res.append("51").toString();
      case PackageNameEqualsFileName:
        return res.append("52").toString();
      case DefinitionNameStartsWithCapitalLetter:
        return res.append("53").toString();
      case NameReference:
        return res.append("54").toString();
      case UniqueName:
        return res.append("55").toString();
      case ImportIsDefined:
        return res.append("56").toString();
      case PackageImportNeedsStar:
        return res.append("58").toString();
      case AmbiguousImport:
        return res.append("59").toString();
      case ImportedElementNameAlreadyExists:
        return res.append("60").toString();
      case ImportDifferentSymbolsWithDuplicateName:
        return res.append("61").toString();
      default:
        Log.error("Internal error: One CoCo was not registered correctly.");
        return res.append("CouldNotResolveErrorCode").toString();
    }
  }
}
