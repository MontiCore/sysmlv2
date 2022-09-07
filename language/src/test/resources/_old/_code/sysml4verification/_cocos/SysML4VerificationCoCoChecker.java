package de.monticore.lang.sysml4verification._cocos;

public class SysML4VerificationCoCoChecker extends SysML4VerificationCoCoCheckerTOP {

  /* Alles, was vor STCreation gecheckt werden kann */
  public static SysML4VerificationCoCoChecker beforeSymbolTableCreation() {
    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    // TODO checker.addCoCo(new SysML4VerificationUpperCaseBlockNameCoCo());
    checker.addCoCo(new TrustLevelRelationOnlyUsesPartProperties());
    checker.addCoCo(new UniqueTrustLevelStatement());
    return checker;
  }

  /* Und alles für danach */
  public static SysML4VerificationCoCoChecker afterSymbolTableCreation() {
    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    // TODO checker.addCoCo(new SysML4VerificationRefinementsExistCoCo());
    checker.addCoCo(new FinalDirection());
    return checker;
  }

}
