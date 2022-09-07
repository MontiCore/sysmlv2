package schrott._cocos;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTAssertConstraintUsageCoCo;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTTypePartStd;
import schrott._ast.ASTFeatureTypingVerification;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ConstraintDefinitionIsPresentCoCo implements SysMLConstraintsASTConstraintUsageCoCo, SysMLConstraintsASTAssertConstraintUsageCoCo {

  private final MCSimpleGenericTypesFullPrettyPrinter typePrinter = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();

  @Override
  public void check(ASTAssertConstraintUsage node) {
    var featureTyping = (ASTFeatureTypingVerification) ((ASTTypePartStd) node.getTypePart()).getFeatureTyping(0);
    String constraintName = featureTyping.getMCType().printType(typePrinter);

    Optional<SysMLTypeSymbol> constraintSymbol = node.getEnclosingScope().resolveSysMLType(constraintName);
    if (constraintSymbol.isEmpty()) {
      Log.error("Can't resolve constraint " + constraintName, featureTyping.get_SourcePositionStart(),
          featureTyping.get_SourcePositionEnd());
    }
  }

  @Override
  public void check(ASTConstraintUsage node) {
    var featureTyping = (ASTFeatureTypingVerification) ((ASTTypePartStd) node
        .getConstraintDeclaration()
        .getSysMLNameAndTypePart()
        .getTypePart())
        .getFeatureTyping(0);
    String constraintName = featureTyping.getMCType().printType(typePrinter);

    Optional<SysMLTypeSymbol> constraintSymbol = node.getEnclosingScope().resolveSysMLType(constraintName);
    if (constraintSymbol.isEmpty()) {
      Log.error("Can't resolve constraint " + constraintName, featureTyping.get_SourcePositionStart(),
          featureTyping.get_SourcePositionEnd());
    }
  }
}
