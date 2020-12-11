package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTImportUnitStdCoCo;
import de.monticore.lang.sysml.cocos.imports.ImportStatementValid;
import de.monticore.lang.sysml.cocos.naming.DefinitionNameStartsWithCapitalLetter;
import de.monticore.lang.sysml.cocos.naming.PackageNameEqualsFileName;
import de.monticore.lang.sysml.cocos.naming.UniqueName;
import de.monticore.lang.sysml.cocos.naming.UsageNameStartsWithLowerCase;
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
    checker.addCoCo(new UsageNameStartsWithLowerCase());
    checker.addCoCo(new UniqueName());
    // checker.addCoCo(new NameReference()); Currently we cannot resolve all Name references, so it is no use to test
    // it.
    checker.addCoCo(new PackageNameEqualsFileName());
    //The following two checker include all checks for imports, because the import nodes save their CoCo Violation
    //while resolving import. This saves a significant amount of computational power, because resolving imports
    //can lead to a lot of nodes being visited.
    checker.addCoCo((SysMLImportsAndPackagesASTAliasPackagedDefinitionMemberCoCo) new ImportStatementValid());
    checker.addCoCo((SysMLImportsAndPackagesASTImportUnitStdCoCo) new ImportStatementValid());




    return checker;
  }



  public static String getErrorCode(SysMLCoCoName name) {
    StringBuilder res = new StringBuilder();
    res.append("0xA71"); //Errorcode are from 0xA7150 - 0xA7200
    switch (name) {
      //Naming
      case DefinitionNameStartsWithCapitalLetter:
        return res.append("50").toString();
      case UsageNameStartsWithLowerCase:
        return res.append("51").toString();
      case UniqueName:
        return res.append("52").toString();
      case NameReference:
        return res.append("53").toString();
      case ArtifactStartsWithPackage:
        return res.append("54").toString();
      case PackageNameEqualsArtifactName:
        return res.append("55").toString();
      //Imports
      case ImportResolves:
        return res.append("56").toString();
      case NoAmbiguousImport:
        return res.append("57").toString();
      case ImportAliasNecessary:
        return res.append("58").toString();
      case ImportedElementNameAlreadyExists:
        return res.append("59").toString();
      case ImportDifferentSymbolsWithDuplicateName:
        return res.append("60").toString();
      case PackageImportWithStar:
        return res.append("61").toString();
      default:
        Log.error("Internal error: One CoCo was not registered correctly.");
        return res.append("CouldNotResolveErrorCode").toString();
    }
  }
}
