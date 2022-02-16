package schrott._cocos;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTConstraintExpressionMember;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintExpressionMemberCoCo;
import schrott.types.check.DeriveSymTypeOfSysMLExpressionDelegator;
import schrott.types.check.SynthesizeTypeSysML4VerificationDelegator;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class ConstraintExpressionTypeIsBooleanCoCo implements SysMLConstraintsASTConstraintExpressionMemberCoCo {

  // Soll Typen für "Typen" berechnen (das "int" in "int a = 1")
  protected final ISynthesize synthesizer = new SynthesizeTypeSysML4VerificationDelegator();

  // Der berechnet Typen von Expressions (das "1" in "int a = 1")
  protected final IDerive calculator = new DeriveSymTypeOfSysMLExpressionDelegator();

  // TypeCheck-Facade initialisieren
  protected final TypeCheck typeCheck = new TypeCheck(synthesizer, calculator);

  // TODO find out if this ASTElement is not used anywhere else (correct one to check for assertions CoCo?)
  @Override
  public void check(ASTConstraintExpressionMember node) {
    try {
      SymTypeExpression result = typeCheck.typeOf(node.getExpression());

      if (!TypeCheck.isBoolean(result)) {
        Log.error("Wrong type.\n"
            + "Found: " + result.print() + "\n"
            + "Expected: boolean",
            node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } catch (Exception e) {
      Log.error("Couldn't derive type of expression.\nExpected: boolean",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
