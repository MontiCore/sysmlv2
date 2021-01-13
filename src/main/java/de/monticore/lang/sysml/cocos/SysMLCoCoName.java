package de.monticore.lang.sysml.cocos;

public enum SysMLCoCoName {
  //Naming
  DefinitionNameStartsWithCapitalLetter, UsageNameStartsWithLowerCase, UniqueName,NameReference,
  ArtifactStartsWithPackage, PackageNameEqualsArtifactName,
  //Import
  ImportResolves, NoAmbiguousImport, ImportAliasNecessary, ImportedElementNameAlreadyExists,
  ImportDifferentSymbolsWithDuplicateName,  PackageImportWithStar,
}
