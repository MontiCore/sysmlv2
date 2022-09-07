package schrott._cocos;

import de.monticore.lang.sysml.ad._ast.ASTActionParameterListStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTAssertConstraintUsageCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTTypePartStd;
import schrott._ast.ASTFeatureTypingVerification;
import schrott._symboltable.ISysML4VerificationScope;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ParameterUsageInAssertIsValidCoCo extends AbstractTypeCheckCoCo implements
    SysMLConstraintsASTAssertConstraintUsageCoCo {

  @Override
  public void check(ASTAssertConstraintUsage node) {
    var featureTyping = (ASTFeatureTypingVerification) ((ASTTypePartStd) node.getTypePart()).getFeatureTyping(0);
    String constraintName = featureTyping.getMCType().printType(typePrinter);

    Optional<SysMLTypeSymbol> constraintSymbol = node.getEnclosingScope().resolveSysMLType(constraintName);
    if (constraintSymbol.isEmpty()) {
      Log.error("Can't resolve constraint " + constraintName, featureTyping.get_SourcePositionStart(),
          featureTyping.get_SourcePositionEnd());
      return;
    }

    if (node.getConstraintParameterPart().isPresentActionParameterList()) {
      var parameters = ((ASTActionParameterListStd) node
          .getConstraintParameterPart()
          .getActionParameterList())
          .getActionParameterMemberAndFlowMemberList();

      super.checkConstraintParameterUsage((ISysML4VerificationScope) node.getEnclosingScope(),
          constraintSymbol.get(), node, parameters);
    }
  }
}
