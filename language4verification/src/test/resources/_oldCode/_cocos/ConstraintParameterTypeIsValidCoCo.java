package schrott._cocos;

import schrott._ast.ASTParameterMemberStd;
import schrott.types.check.SynthesizeTypeSysML4VerificationDelegator;

/**
 * Checkt, dass Typen in der Definition eines Constraints synthetisierbar sind (also einfach gesagt bekannt sind)
 */
public class ConstraintParameterTypeIsValidCoCo implements SysML4VerificationASTParameterMemberStdCoCo {

  @Override
  public void check(ASTParameterMemberStd node) {
    node.accept(new SynthesizeTypeSysML4VerificationDelegator().getTraverser());
  }
}
