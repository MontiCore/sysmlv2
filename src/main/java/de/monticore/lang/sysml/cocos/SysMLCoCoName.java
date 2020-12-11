package de.monticore.lang.sysml.cocos;

import de.monticore.lang.sysml.cocos.naming.DefinitionNameStartsWithCapitalLetter;
import de.monticore.lang.sysml.cocos.naming.PackageNameEqualsFileName;

public enum SysMLCoCoName {
  //Naming
  DefinitionNameStartsWithCapitalLetter, UsageNameStartsWithLowerCase, UniqueName,NameReference,
  ArtifactStartsWithPackage, PackageNameEqualsArtifactName,
  //Import
  ImportResolves, NoAmbiguousImport, ImportAliasNecessary, ImportedElementNameAlreadyExists,
  ImportDifferentSymbolsWithDuplicateName,  PackageImportWithStar,
}
