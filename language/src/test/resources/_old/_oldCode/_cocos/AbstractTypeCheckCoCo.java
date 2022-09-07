package schrott._cocos;

import de.monticore.lang.sysml.ad._ast.ASTActionParameterMemberAndFlowMember;
import de.monticore.lang.sysml.ad._ast.ASTParameterListStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTSysMLConstraintsNode;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTParameterMember;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTFeatureValue;
import schrott._ast.ASTConstraintDefinition;
import schrott._ast.ASTParameterMemberStd;
import schrott._symboltable.ISysML4VerificationScope;
import schrott.types.check.DeriveSymTypeOfSysMLExpressionDelegator;
import schrott.types.check.SynthesizeTypeSysML4VerificationDelegator;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTypeCheckCoCo {

  protected final MCSimpleGenericTypesFullPrettyPrinter typePrinter = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();

  // Soll Typen für "Typen" berechnen (das "int" in "int a = 1")
  protected final ISynthesize synthesizer = new SynthesizeTypeSysML4VerificationDelegator();

  // Der berechnet Typen von Expressions (das "1" in "int a = 1")
  protected final IDerive calculator = new DeriveSymTypeOfSysMLExpressionDelegator();

  // TypeCheck-Facade initialisieren
  protected final TypeCheck typeCheck = new TypeCheck(synthesizer, calculator);

  /**
   * Nutzt das constraintSymbol, um Parameterliste der Definition zu holen und gleicht diese (insbesondere auf Typen)
   * mit der Parameterliste des ConstraintUsages ab. Die Methode ist sowohl für Assertions, als auch "Vanilla Usages",
   * geeignet. Siehe auch {@link ParameterUsageIsValidCoCo} und {@link ParameterUsageInAssertIsValidCoCo}.
   *
   * @param constraintUsageScope Scope der node aus der CoCo wo wir gerade den Check durchführen
   * @param constraintSymbol Symbol der Constraint Definition zum "Nachgucken" der Parameterliste
   * @param usageNode Node bei welcher die CoCo checkt. Wird benötigt für Error Logging
   * @param constraintUsageParameters Liste von "Assignments" mit `Parametername = Expression`,
   *                                  die beim ConstraintUsage auftauchen
   */
  protected void checkConstraintParameterUsage(ISysML4VerificationScope constraintUsageScope,
                                               SysMLTypeSymbol constraintSymbol,
                                               ASTSysMLConstraintsNode usageNode,
                                               List<ASTActionParameterMemberAndFlowMember> constraintUsageParameters) {
    ASTConstraintDefinition constraintDefNode = (ASTConstraintDefinition) constraintSymbol.getAstNode();
    if (constraintDefNode.getConstraintDefDeclaration().isPresentParameterList()) {
      List<ASTParameterMember> constraintDefParameters = ((ASTParameterListStd) constraintDefNode
          .getConstraintDefDeclaration()
          .getParameterList())
          .streamParameterMembers()
          .collect(Collectors.toList());

      if (constraintDefParameters.size() != constraintUsageParameters.size()) {
        Log.error("Usage doesn't have the same number of parameters as definition "
            + constraintDefNode.getName(), usageNode.get_SourcePositionStart(), usageNode.get_SourcePositionEnd());
        return;
      }

      for (int i = 0, constraintDefParametersSize = constraintDefParameters.size();
           i < constraintDefParametersSize; i++) {
        var constraintUsageParameter = constraintUsageParameters.get(i);
        ASTFeatureValue featureValue = constraintUsageParameter.getActionParameterMember()
            .getActionParameter()
            .getValuePart()
            .getFeatureValue();
        featureValue.getExpression().accept(calculator.getTraverser());

        if (calculator.getResult().isEmpty()) {
          Log.error("Couldn't derive type of expression", featureValue.get_SourcePositionStart(),
              featureValue.get_SourcePositionEnd());
          return;
        }

        calculator.getResult().get().getTypeInfo().setEnclosingScope(constraintUsageScope);

        SymTypeExpression defParameterType = ((ASTParameterMemberStd)
            constraintDefParameters.get(i)).getSymbol().getType();
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
}
