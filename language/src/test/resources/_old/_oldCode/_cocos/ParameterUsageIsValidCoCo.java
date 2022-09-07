package schrott._cocos;

import de.monticore.lang.sysml.ad._ast.ASTActionParameterListStd;
import de.monticore.lang.sysml.ad._ast.ASTActionParameterMemberAndFlowMember;
import de.monticore.lang.sysml.ad._ast.ASTParameterListStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTSysMLConstraintsNode;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTAssertConstraintUsageCoCo;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTParameterMember;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTFeatureValue;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTTypePartStd;
import schrott._ast.ASTConstraintDefinition;
import schrott._ast.ASTFeatureTypingVerification;
import schrott._ast.ASTParameterMemberStd;
import schrott.types.check.DeriveSymTypeOfSysMLExpressionDelegator;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Prüft, dass Parameter-Belegungen einer "assert"'eten Constraint-Usage zu deren Definition passen.
 *
 * WICHTIG: Wenn diese CoCo genutzt wird, muss sie zwei mal an den Checker gegeben werden.
 * Einmal gecastet zu `SysMLConstraintsASTConstraintUsageCoCo` und einmal zu `SysMLConstraintsASTAssertConstraintUsageCoCo`
 */
public class ParameterUsageIsValidCoCo implements SysMLConstraintsASTConstraintUsageCoCo, SysMLConstraintsASTAssertConstraintUsageCoCo {

  // Berechnet Typen von Expressions
  private final IDerive calculator = new DeriveSymTypeOfSysMLExpressionDelegator();

  private final MCSimpleGenericTypesFullPrettyPrinter typePrinter = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();

  /**
   * Nutzt das constraintSymbol, um Parameterliste der Definition zu holen und gleicht diese (insbesondere auf Typen)
   * mit der Parameterliste des ConstraintUsages ab. Die Methode ist sowohl für "assert'ete" und "nicht-assert'ete" Constraints
   * geeignet. Sie checkt die folgenden Sachen:
   *  - Constraint und sein Usage haben die gleiche Anzahl Parameter
   *  - Typen von Expressions, die im Usage verwendet werden, müssen berechenbar sein
   *  - Typ von Parameter im Usage muss gleich sein mit definiertem Typen im Constraint
   *
   * @param constraintSymbol Symbol der Constraint Definition zum "Nachgucken" der Parameterliste
   * @param usageNode Node bei welcher die CoCo checkt. Wird benötigt für Error Logging
   * @param constraintUsageParameters Liste von "Assignments" mit `Parametername = Expression`,
   *                                  die beim ConstraintUsage auftauchen
   */
  private void checkUsage(
      SysMLTypeSymbol constraintSymbol,
      ASTSysMLConstraintsNode usageNode,
      List<ASTActionParameterMemberAndFlowMember> constraintUsageParameters)
  {
    ASTConstraintDefinition constraintDefNode = (ASTConstraintDefinition) constraintSymbol.getAstNode();
    if (constraintDefNode.getConstraintDefDeclaration().isPresentParameterList()) {
      List<ASTParameterMember> constraintDefParameters = ((ASTParameterListStd) constraintDefNode
          .getConstraintDefDeclaration()
          .getParameterList())
          .streamParameterMembers()
          .collect(Collectors.toList());

      if (constraintDefParameters.size() < constraintUsageParameters.size()) {
        Log.error("Too many arguments for " + constraintSymbol.getFullName() + ".\n"
            + "Found: " + constraintUsageParameters.size() + "\n"
            + "Expected: " + constraintDefParameters.size(),
            usageNode.get_SourcePositionStart(), usageNode.get_SourcePositionEnd());
        return;
      } else if (constraintDefParameters.size() > constraintUsageParameters.size()) {
        StringBuilder errorMessage = new StringBuilder();
        for (int i = constraintUsageParameters.size(); i < constraintDefParameters.size(); i++) {
          errorMessage.append("Missing argument for ")
              .append(((ASTParameterMemberStd) constraintDefParameters.get(i)).getName())
              .append("\n");
        }

        Log.error(errorMessage.toString(), usageNode.get_SourcePositionStart(), usageNode.get_SourcePositionEnd());
        return;
      }

      for (int i = 0, constraintDefParametersSize = constraintDefParameters.size(); i < constraintDefParametersSize; i++) {
        ASTFeatureValue featureValue = constraintUsageParameters.get(i)
            .getActionParameterMember()
            .getActionParameter()
            .getValuePart()
            .getFeatureValue();
        featureValue.getExpression().accept(calculator.getTraverser());

        if (calculator.getResult().isEmpty()) {
          Log.error("Couldn't derive type of expression.\nExpected: "
                  + ((ASTParameterMemberStd) constraintDefParameters.get(i)).getSymbol().getType(),
              featureValue.get_SourcePositionStart(), featureValue.get_SourcePositionEnd());
          return;
        }

        calculator.getResult().get()
            .getTypeInfo()
            .setEnclosingScope((IBasicSymbolsScope) usageNode.getEnclosingScope());

        SymTypeExpression defParameterType = ((ASTParameterMemberStd) constraintDefParameters.get(i))
            .getSymbol()
            .getType();

        if (!TypeCheck.compatible(defParameterType, calculator.getResult().get())) {
          Log.error("Parameter has wrong type.\n"
                  + "Found: " + calculator.getResult().get().print() + "\n"
                  + "Required: " + defParameterType.print(),
              featureValue.get_SourcePositionStart(),
              featureValue.get_SourcePositionEnd());
          return;
        }
      }
    }
  }

  /**
   * Der Check wird immer aufgerufen, sobald wir eine {@link ASTConstraintUsage} Node finden. Das Vorgehen ist dann:
   *  - Definition zum genutzen Constraint "fetchen"
   *  - Sicherstellen, dass "referenzierter" Constraint definiert ist
   *  - Wenn Parameter existieren, werden mehrere Sachen
   *    mit {@link ParameterUsageIsValidCoCo#checkUsage} geprüft
   */
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
      Log.trace("Can't resolve constraint " + constraintName, this.getClass().getName());
      return;
    }

    if (node.getConstraintDeclaration().getConstraintParameterPart().isPresentActionParameterList()) {
      var parameters = ((ASTActionParameterListStd) node
          .getConstraintDeclaration()
          .getConstraintParameterPart()
          .getActionParameterList())
          .getActionParameterMemberAndFlowMemberList();

      checkUsage(constraintSymbol.get(), node, parameters);
    }
  }

  /**
   * Der Check wird immer aufgerufen, sobald wir eine {@link ASTAssertConstraintUsage} Node finden.
   * Wichtig ist hier, dass ein Unterscheid zwischen {@link ASTAssertConstraintUsage} und {@link ASTConstraintUsage}
   * besteht. Das Vorgehen ist dann:
   *  - Definition zum genutzen Constraint "fetchen"
   *  - Sicherstellen, dass "referenzierter" Constraint definiert ist
   *  - Wenn Parameter existieren, werden mehrere Sachen
   *    mit {@link ParameterUsageIsValidCoCo#checkUsage} geprüft
   */
  @Override
  public void check(ASTAssertConstraintUsage node) {
    var featureTyping = (ASTFeatureTypingVerification) ((ASTTypePartStd) node.getTypePart()).getFeatureTyping(0);
    String constraintName = featureTyping.getMCType().printType(typePrinter);

    Optional<SysMLTypeSymbol> constraintSymbol = node.getEnclosingScope().resolveSysMLType(constraintName);
    if (constraintSymbol.isEmpty()) {
      Log.trace("Can't resolve constraint " + constraintName, this.getClass().getName());
      return;
    }

    if (node.getConstraintParameterPart().isPresentActionParameterList()) {
      var parameters = ((ASTActionParameterListStd) node
          .getConstraintParameterPart()
          .getActionParameterList())
          .getActionParameterMemberAndFlowMemberList();

      checkUsage(constraintSymbol.get(), node, parameters);
    }
  }
}
